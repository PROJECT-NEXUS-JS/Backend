package com.example.nexus.app.account.service;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.user.domain.SocialType;
import com.example.nexus.app.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoUnlinkService {

    private final RestTemplate restTemplate;

    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";

    /**
     * 사용자 토큰으로 카카오 연동 해제
     * @param user 카카오 연동 해제할 사용자
     * @param accessToken 사용자가 제공한 카카오 액세스 토큰
     */
    public void unlinkKakaoAccount(User user, String accessToken) {
        if (user.getSocialType() != SocialType.KAKAO) {
            log.warn("카카오 계정이 아닌 사용자에 대한 연동 해제 시도: userId={}, socialType={}", 
                    user.getId(), user.getSocialType());
            return;
        }

        if (accessToken == null || accessToken.trim().isEmpty()) {
            log.warn("카카오 액세스 토큰이 제공되지 않음: userId={}", user.getId());
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_UNLINK_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("카카오 연동 해제 성공: userId={}, oauthId={}", user.getId(), user.getOauthId());
            } else {
                log.error("카카오 연동 해제 실패: userId={}, statusCode={}", user.getId(), response.getStatusCode());
                // 연동 해제 실패해도 계정 탈퇴는 진행 (토큰이 만료되었을 수 있음)
                log.warn("카카오 연동 해제 실패했지만 계정 탈퇴는 계속 진행합니다.");
            }

        } catch (Exception e) {
            log.error("카카오 연동 해제 중 오류 발생: userId={}, error={}", user.getId(), e.getMessage());
            // 연동 해제 실패해도 계정 탈퇴는 진행
            log.warn("카카오 연동 해제 중 오류가 발생했지만 계정 탈퇴는 계속 진행합니다.");
        }
    }
}
