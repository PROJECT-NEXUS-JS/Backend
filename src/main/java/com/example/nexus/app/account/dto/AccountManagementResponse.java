package com.example.nexus.app.account.dto;

import com.example.nexus.app.account.domain.AccountInfo;
import com.example.nexus.app.mypage.domain.UserProfile;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.domain.SocialType;
import lombok.Builder;

import java.util.List;

@Builder
public record AccountManagementResponse(
    // 기본정보
    String nickname,
    String profileUrl,
    
    // 개인정보
    String email,
    String phoneNumber,
    String connectedAccount,
    
    // 맞춤정보
    String job,
    String birthYear,
    String gender,
    List<String> interests,
    List<String> preferredGenres
) {
    public static AccountManagementResponse from(User user, UserProfile userProfile, AccountInfo accountInfo) {
        return AccountManagementResponse.builder()
            .nickname(user.getNickname())
            .profileUrl(user.getProfileUrl())
            .email(user.getEmail())
            .phoneNumber(accountInfo != null ? accountInfo.getPhoneNumber() : null)
            .connectedAccount(formatConnectedAccount(user.getSocialType(), user.getEmail()))
            .job(userProfile != null ? userProfile.getJob() : null)
            .birthYear(accountInfo != null ? accountInfo.getBirthYear() : null)
            .gender(accountInfo != null ? accountInfo.getGender() : null)
            .interests(userProfile != null ? userProfile.getInterests() : List.of())
            .preferredGenres(accountInfo != null ? accountInfo.getPreferredGenres() : List.of())
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
