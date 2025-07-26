package com.example.nexus.app.global.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    private static final int DEFAULT_VIEW_COOKIE_MAX_AGE = 24 * 60 * 60; // 24시간
    private static final boolean DEFAULT_COOKIE_SECURE = false; // 운영환경에서 true 처리 필요
    private static final String VIEW_COOKIE_PREFIX = "viewed_post_";

    /**
     * 조회수 증가 가능 여부 확인 및 쿠키 설정
     */
    public boolean shouldIncrementViewAndSetCookie(Long postId, HttpServletRequest request, HttpServletResponse response) {
        String cookieName = generateViewCookieName(postId);

        // 기존 쿠키 확인
        if (hasCookie(request, cookieName)) {
            return false;
        }

        // 새로운 조회 쿠키 설정
        setViewCookie(response, cookieName);
        return true;
    }

    /**
     * 쿠키 존재 여부 확인
     */
    public boolean hasCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return false;
        }

        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 조회 기록 쿠키 설정
     */
    public void setViewCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "viewed");
        cookie.setMaxAge(DEFAULT_VIEW_COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(DEFAULT_COOKIE_SECURE);

        response.addCookie(cookie);
    }

    /**
     * 게시글 조회 쿠키명 생성
     */
    public String generateViewCookieName(Long postId) {
        return VIEW_COOKIE_PREFIX + postId;
    }
}
