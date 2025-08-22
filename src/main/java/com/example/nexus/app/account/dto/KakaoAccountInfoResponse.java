package com.example.nexus.app.account.dto;

import com.example.nexus.app.user.domain.SocialType;
import com.example.nexus.app.user.domain.User;
import lombok.Builder;

@Builder
public record KakaoAccountInfoResponse(
    String connectedAccount,
    String email,
    String nickname,
    String profileUrl,
    boolean isKakaoAccount
) {
    public static KakaoAccountInfoResponse from(User user) {
        return KakaoAccountInfoResponse.builder()
            .connectedAccount(formatConnectedAccount(user.getSocialType(), user.getEmail()))
            .email(user.getEmail())
            .nickname(user.getNickname())
            .profileUrl(user.getProfileUrl())
            .isKakaoAccount(user.getSocialType() == SocialType.KAKAO)
            .build();
    }

    private static String formatConnectedAccount(SocialType socialType, String email) {
        if (socialType == null) return null;
        
        switch (socialType) {
            case KAKAO:
                return "카카오 (" + email + ")";
            case GOOGLE:
                return "구글 (" + email + ")";
            case APPLE:
                return "애플 (" + email + ")";
            default:
                return socialType.name() + " (" + email + ")";
        }
    }
}
