package com.example.nexus.app.global.oauth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.domain.SocialType;
import com.example.nexus.app.user.repository.UserRepository;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.oauth.domain.IdTokenAttributes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class IdTokenService {

    private final JwtDecoder kakaoJwtDecoder;
    private final JwtDecoder googleJwtDecoder;
    private final JwtDecoder appleJwtDecoder;
    private final UserRepository userRepository;

    public CustomUserDetails loadUserByAccessToken(String accessToken){

        DecodedJWT decodedJWT;
        SocialType socialType;
        User findUser;

        try {

            decodedJWT = JWT.decode(accessToken);
            socialType = checkIssuer(decodedJWT.getIssuer());

            Map<String, Object> attributes = tokenToattributes(accessToken, socialType);
            IdTokenAttributes idTokenAttributes = new IdTokenAttributes(attributes, socialType);

            findUser = checkUser(idTokenAttributes);
        } catch (Exception e) {
            throw new RuntimeException("엑세스 토큰 인증 오류 " + e.getMessage());
        }
        return new CustomUserDetails(
                Collections.singleton(new SimpleGrantedAuthority(findUser.getRoleType().toString())),
                findUser.getEmail(),
                findUser.getRoleType(),
                findUser.getId()
        );
    }

    private SocialType checkIssuer(String issuer){
        if(issuer.equals("https://kauth.kakao.com")) return SocialType.KAKAO;
        else if(issuer.equals("https://accounts.google.com")) return SocialType.GOOGLE;
        return SocialType.APPLE;
    }

    private User checkUser(IdTokenAttributes idTokenAttributes){
        User findUser = userRepository.findByEmail(idTokenAttributes.getUserInfo().getEmail()).orElse(null);
        if (findUser == null) return createUser(idTokenAttributes);
        return findUser;
    }

    private User createUser(IdTokenAttributes idTokenAttributes) {
        User createdUser = idTokenAttributes.toUser();
        return userRepository.save(createdUser);
    }

    private Map<String, Object> tokenToattributes(String idToken, SocialType socialType){
        if(socialType == SocialType.GOOGLE) return googleJwtDecoder.decode(idToken).getClaims();
        if(socialType == SocialType.KAKAO) return kakaoJwtDecoder.decode(idToken).getClaims();
        if(socialType == SocialType.APPLE) return appleJwtDecoder.decode(idToken).getClaims();
        return null;
    }
}
