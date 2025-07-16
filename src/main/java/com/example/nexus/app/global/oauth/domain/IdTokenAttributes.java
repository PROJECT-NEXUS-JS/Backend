package com.example.nexus.app.global.oauth.domain;

import com.example.nexus.app.global.oauth.domain.userinfo.AppleUserInfo;
import com.example.nexus.app.global.oauth.domain.userinfo.GoogleUserInfo;
import com.example.nexus.app.global.oauth.domain.userinfo.KakaoUserInfo;
import com.example.nexus.app.global.oauth.domain.userinfo.UserInfo;
import com.example.nexus.app.user.domain.RoleType;
import com.example.nexus.app.user.domain.SocialType;
import com.example.nexus.app.user.domain.User;
import java.util.Map;
import lombok.Getter;

@Getter
public class IdTokenAttributes {

    private UserInfo userInfo;
    private SocialType socialType;

    public IdTokenAttributes(Map<String, Object> attributes, SocialType socialType){
        this.socialType = socialType;
        if(socialType == SocialType.GOOGLE) this.userInfo = new GoogleUserInfo(attributes);
        if(socialType == SocialType.KAKAO) this.userInfo = new KakaoUserInfo(attributes);
        if(socialType == SocialType.APPLE) this.userInfo = new AppleUserInfo(attributes);
    }

    public User toUser() {
        return User.builder()
                .socialType(socialType)
                .oauthId(userInfo.getId())
                .nickname("")
                .profileUrl(null)
                .email(userInfo.getEmail())
                .roleType(RoleType.ROLE_GUEST)
                .build();
    }
}
