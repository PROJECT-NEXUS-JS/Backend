package com.example.nexus.app.global.oauth.service;

import com.example.nexus.app.global.code.dto.LoginResponseDto;
import com.example.nexus.app.global.code.dto.UserInfoResponseDto;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.security.JwtService;
import com.example.nexus.app.global.security.TokenBlacklistService;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final IdTokenService idTokenService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

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
        return new UserInfoResponseDto(user.getEmail(), user.getNickname(), user.getProfileUrl(), lastLoginAtStr, user.getRoleType());
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
