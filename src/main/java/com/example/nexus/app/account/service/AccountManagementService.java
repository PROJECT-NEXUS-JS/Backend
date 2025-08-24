package com.example.nexus.app.account.service;

import com.example.nexus.app.account.domain.AccountInfo;
import com.example.nexus.app.account.dto.AccountManagementResponse;
import com.example.nexus.app.account.dto.BasicInfoUpdateRequest;
import com.example.nexus.app.account.dto.CustomInfoUpdateRequest;
import com.example.nexus.app.account.dto.PersonalInfoUpdateRequest;
import com.example.nexus.app.account.dto.KakaoAccountInfoResponse;
import com.example.nexus.app.account.repository.AccountInfoRepository;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.s3.S3UploadService;
import com.example.nexus.app.mypage.domain.UserProfile;
import com.example.nexus.app.mypage.repository.UserProfileRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AccountManagementService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AccountInfoRepository accountInfoRepository;
    private final S3UploadService s3UploadService;
    private final KakaoUnlinkService kakaoUnlinkService;

    /**
     * 계정관리 정보 조회
     */
    public AccountManagementResponse getAccountManagementInfo(Long userId) {
        User user = getUser(userId);
        
        // 탈퇴된 사용자인지 확인
        if (user.isWithdrawn()) {
            throw new GeneralException(ErrorStatus.USER_ALREADY_WITHDRAWN);
        }
        
        UserProfile userProfile = null;
        AccountInfo accountInfo = null;
        
        try {
            userProfile = userProfileRepository.findByUserWithInterests(user).orElse(null);
        } catch (Exception e) {
            log.warn("UserProfile 조회 실패: {}", e.getMessage());
        }
        
        try {
            accountInfo = accountInfoRepository.findByUserWithPreferredGenres(user).orElse(null);
        } catch (Exception e) {
            log.warn("AccountInfo 조회 실패: {}", e.getMessage());
        }
        
        return AccountManagementResponse.from(user, userProfile, accountInfo);
    }

    /**
     * 카카오 계정 정보 조회
     */
    public KakaoAccountInfoResponse getKakaoAccountInfo(Long userId) {
        User user = getUser(userId);
        return KakaoAccountInfoResponse.from(user);
    }

    /**
     * 기본정보 수정 (활동명, 프로필 이미지)
     */
    @Transactional
    public AccountManagementResponse updateBasicInfo(Long userId, BasicInfoUpdateRequest request, MultipartFile profileImage) {
        User user = getUser(userId);
        
        // 활동명 수정
        if (request.nickname() != null && !request.nickname().trim().isEmpty()) {
            user.updateNickname(request.nickname());
        }
        
        // 프로필 이미지 수정
        if (profileImage != null && !profileImage.isEmpty()) {
            validateImageFile(profileImage);
            
            // 기존 이미지 삭제
            if (user.getProfileUrl() != null && !isDefaultProfileImage(user.getProfileUrl())) {
                try {
                    s3UploadService.deleteFile(user.getProfileUrl());
                } catch (Exception e) {
                    log.warn("기존 프로필 이미지 삭제 실패: {}", e.getMessage());
                }
            }
            
            // 새 이미지 업로드
            String imageUrl = s3UploadService.uploadFile(profileImage);
            user.updateProfileImage(imageUrl);
        }
        
        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(null);
        AccountInfo accountInfo = accountInfoRepository.findByUser(user).orElse(null);
        
        return AccountManagementResponse.from(user, userProfile, accountInfo);
    }

    /**
     * 개인정보 수정 (전화번호)
     * 카카오 계정의 경우 이메일은 카카오에서 관리하므로 수정 불가
     */
    @Transactional
    public AccountManagementResponse updatePersonalInfo(Long userId, PersonalInfoUpdateRequest request) {
        User user = getUser(userId);
        AccountInfo accountInfo = getOrCreateAccountInfo(user);
        
        if (request.phoneNumber() != null) {
            accountInfo.updatePhoneNumber(request.phoneNumber());
        }
        
        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(null);
        
        return AccountManagementResponse.from(user, userProfile, accountInfo);
    }

    /**
     * 맞춤정보 수정
     */
    @Transactional
    public AccountManagementResponse updateCustomInfo(Long userId, CustomInfoUpdateRequest request) {
        User user = getUser(userId);
        UserProfile userProfile = getOrCreateUserProfile(user);
        AccountInfo accountInfo = getOrCreateAccountInfo(user);
        
        // UserProfile 업데이트
        userProfile.update(request.job(), request.interests());
        
        // AccountInfo 업데이트
        accountInfo.updateBirthYear(request.birthYear());
        accountInfo.updateGender(request.gender());
        accountInfo.updatePreferredGenres(request.preferredGenres());
        
        return AccountManagementResponse.from(user, userProfile, accountInfo);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(Long userId) {
        User user = getUser(userId);
        // 리프레시 토큰 삭제
        user.updateRefreshToken(null);
    }



    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private UserProfile getOrCreateUserProfile(User user) {
        return userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = UserProfile.builder()
                            .user(user)
                            .job("")
                            .interests(new ArrayList<>())
                            .build();
                    return userProfileRepository.save(newProfile);
                });
    }

    private AccountInfo getOrCreateAccountInfo(User user) {
        return accountInfoRepository.findByUser(user)
                .orElseGet(() -> {
                    AccountInfo newAccountInfo = AccountInfo.builder()
                            .user(user)
                            .phoneNumber("")
                            .birthYear("")
                            .gender("")
                            .preferredGenres(new ArrayList<>())
                            .build();
                    return accountInfoRepository.save(newAccountInfo);
                });
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new GeneralException(ErrorStatus.FILE_IS_EMPTY);
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new GeneralException(ErrorStatus.INVALID_IMAGE_FORMAT);
        }
        
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new GeneralException(ErrorStatus.IMAGE_SIZE_EXCEEDED);
        }
    }

    private boolean isDefaultProfileImage(String profileUrl) {
        return profileUrl != null && profileUrl.contains("default");
    }
}
