package com.example.nexus.app.global.oauth.service;

import com.example.nexus.app.account.dto.AccountManagementResponse;
import com.example.nexus.app.account.dto.BasicInfoUpdateRequest;
import com.example.nexus.app.account.dto.CustomInfoUpdateRequest;
import com.example.nexus.app.account.dto.PersonalInfoUpdateRequest;
import com.example.nexus.app.account.dto.KakaoAccountInfoResponse;
import com.example.nexus.app.account.service.AccountManagementService;
import com.example.nexus.app.account.service.KakaoUnlinkService;
import com.example.nexus.app.global.code.dto.LoginResponseDto;
import com.example.nexus.app.global.code.dto.UserInfoResponseDto;
import com.example.nexus.app.global.code.dto.UserInfoUpdateRequest;
import com.example.nexus.app.global.code.dto.RegistrationInfoUpdateRequest; // 추가
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.security.JwtService;
import com.example.nexus.app.global.security.TokenBlacklistService;
import com.example.nexus.app.global.s3.S3UploadService;
import com.example.nexus.app.mypage.domain.UserProfile;
import com.example.nexus.app.mypage.repository.UserProfileRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.domain.RoleType;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final IdTokenService idTokenService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final KakaoUnlinkService kakaoUnlinkService;
    private final S3UploadService s3UploadService;
    private final AccountManagementService accountManagementService;

    public LoginResponseDto login(String idToken) {
        CustomUserDetails userDetails = idTokenService.loadUserByAccessToken(idToken);
        String email = userDetails.getUsername();
        Long userId = userDetails.getUserId();

        String accessToken = jwtService.createAccessToken(email, userId);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.updateRefreshToken(email, refreshToken);
        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        String lastLoginAtStr = (user.getLastLoginAt() != null) ? user.getLastLoginAt().toString() : null;

        // UserProfile 정보 조회 - JOIN FETCH 사용
        String job = null;
        List<String> interests = new ArrayList<>();

        try {
            UserProfile userProfile = userProfileRepository.findByUserWithInterests(user).orElse(null);
            if (userProfile != null) {
                job = userProfile.getJob();
                interests = userProfile.getInterests();
            }
        } catch (Exception e) {
            log.warn("UserProfile 조회 실패: {}", e.getMessage());
            // UserProfile 조회 실패 시에도 기본 정보는 반환
        }

        return new UserInfoResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getProfileUrl(),
                lastLoginAtStr,
                user.getRoleType(),
                job,
                interests
        );
    }

    @Transactional
    public UserInfoResponseDto updateUserInfo(Long userId, UserInfoUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 닉네임 중복 검사
        if (!user.getNickname().equals(request.getNickname()) &&
                userRepository.existsByNickname(request.getNickname())) {
            throw new GeneralException(ErrorStatus.DUPLICATE_NICKNAME);
        }

        // 관심사 검증
        validateInterests(request.getInterests());

        // 닉네임 업데이트
        user.updateNickname(request.getNickname());

        // UserProfile 업데이트 또는 생성
        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(null);
        if (userProfile == null) {
            userProfile = UserProfile.builder()
                    .user(user)
                    .job(request.getJob())
                    .interests(request.getInterests())
                    .build();
            userProfileRepository.save(userProfile);
        } else {
            userProfile.update(request.getJob(), request.getInterests());
        }

        // 업데이트된 정보 반환
        return getUserInfo(userId);
    }

    @Transactional
    public UserInfoResponseDto changeRole(Long userId, RegistrationInfoUpdateRequest request) { // 수정
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        validateInterests(request.getInterests());

        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(null);
        if (userProfile == null) {
            userProfile = UserProfile.builder()
                    .user(user)
                    .job(request.getJob())
                    .interests(request.getInterests())
                    .build();
            userProfileRepository.save(userProfile);
        } else {
            userProfile.update(request.getJob(), request.getInterests());
        }

        user.updateRole(RoleType.ROLE_USER);

        return getUserInfo(userId);
    }

    // 계정관리 관련 메서드들
    @Transactional(readOnly = true)
    public AccountManagementResponse getAccountManagementInfo(Long userId) {
        return accountManagementService.getAccountManagementInfo(userId);
    }

    @Transactional(readOnly = true)
    public KakaoAccountInfoResponse getKakaoAccountInfo(Long userId) {
        return accountManagementService.getKakaoAccountInfo(userId);
    }

    @Transactional
    public AccountManagementResponse updateBasicInfo(Long userId, BasicInfoUpdateRequest request, MultipartFile profileImage) {
        return accountManagementService.updateBasicInfo(userId, request, profileImage);
    }

    @Transactional
    public AccountManagementResponse updatePersonalInfo(Long userId, PersonalInfoUpdateRequest request) {
        return accountManagementService.updatePersonalInfo(userId, request);
    }

    @Transactional
    public AccountManagementResponse updateCustomInfo(Long userId, CustomInfoUpdateRequest request) {
        return accountManagementService.updateCustomInfo(userId, request);
    }

    @Transactional
    public void logout(Long userId, String accessToken) {
        // 액세스 토큰을 블랙리스트에 추가
        if (accessToken != null && !accessToken.isEmpty()) {
            tokenBlacklistService.blacklistToken(accessToken);
        }
        accountManagementService.logout(userId);
    }

    /**
     * 계정 탈퇴
     * 카카오 계정의 경우 카카오 연동 해제도 함께 진행
     */
    @Transactional
    public void withdrawAccount(Long userId, String confirmation, String kakaoAccessToken) {
        if (!"계정 탈퇴".equals(confirmation)) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 이미 탈퇴된 사용자인지 확인
        if (user.isWithdrawn()) {
            throw new GeneralException(ErrorStatus.USER_ALREADY_WITHDRAWN);
        }

        // 카카오 계정인 경우 연동 해제
        if (user.getSocialType() != null && user.getSocialType().name().equals("KAKAO")) {
            try {
                kakaoUnlinkService.unlinkKakaoAccount(user, kakaoAccessToken);
            } catch (Exception e) {
                log.warn("카카오 연동 해제 중 오류 발생했지만 계정 탈퇴는 계속 진행합니다: userId={}, error={}",
                        userId, e.getMessage());
            }
        }

        // 프로필 이미지가 있다면 S3에서 삭제
        if (user.getProfileUrl() != null && !isDefaultProfileImage(user.getProfileUrl())) {
            try {
                s3UploadService.deleteFile(user.getProfileUrl());
            } catch (Exception e) {
                log.warn("프로필 이미지 삭제 실패했지만 계정 탈퇴는 계속 진행합니다: userId={}, error={}",
                        userId, e.getMessage());
            }
        }

        // 계정 상태를 탈퇴로 변경
        user.withdrawAccount();

        // 관련 데이터 삭제
        userProfileRepository.findByUser(user).ifPresent(userProfileRepository::delete);

        log.info("계정 탈퇴 완료: userId={}, email={}", userId, user.getEmail());
    }

    /**
     * 관심사 리스트의 각 항목 길이를 검증하는 private 메소드
     * @param interests 검증할 관심사 리스트
     */
    private void validateInterests(List<String> interests) {
        for (String interest : interests) {
            if (interest.length() > 15) {
                throw new GeneralException(ErrorStatus.INTEREST_LENGTH_EXCEEDED);
            }
        }
    }

    private boolean isDefaultProfileImage(String profileUrl) {
        return profileUrl != null && (profileUrl.contains("default") || profileUrl.contains("kakao"));
    }

    public LoginResponseDto reissueTokens(String oldAccessToken, String oldRefreshToken) {
        if (!jwtService.isTokenValid(oldRefreshToken)) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN);
        }

        User user = userRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EXPIRED_TOKEN));

        String newAccessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        String newRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(newRefreshToken);

        tokenBlacklistService.blacklistToken(oldAccessToken);
        tokenBlacklistService.blacklistToken(oldRefreshToken);

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }
}
