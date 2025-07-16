package com.example.nexus.app.global.oauth.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTestControllerDto { // 실제 환경서는 삭제 예정
    private String accessToken;
    private String refreshToken;
}

