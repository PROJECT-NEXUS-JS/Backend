package com.example.nexus.app.user.service;

import com.example.nexus.app.global.code.dto.LoginResponseDto;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.security.JwtService;
import com.example.nexus.app.mypage.domain.UserProfile;
import com.example.nexus.app.mypage.repository.UserProfileRepository;
import com.example.nexus.app.user.domain.RoleType;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.dto.ProfileRequestDto;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final JwtService jwtService;

    @Transactional
    public LoginResponseDto completeSignUp(Long userId, ProfileRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (user.getRoleType() != RoleType.ROLE_GUEST) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .job(requestDto.getJob())
                .interests(requestDto.getInterests())
                .build();
        userProfileRepository.save(userProfile);

        user.updateRole(RoleType.ROLE_USER);

        String newAccessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        String newRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(newRefreshToken);

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }
}