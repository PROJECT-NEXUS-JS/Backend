package com.example.nexus.app.global.oauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "User OIDC Login", description = "OIDC Login API")
public class AuthController {

    @GetMapping("/login")
    @Operation(summary = "소셜 로그인", description = "OIDC 전송하여 로그인하는 소셜 로그인 API")
    @Parameter(
            name = "id_token",
            description = "Bearer 토큰을 제외한 OIDC Token 헤더",
            required = true,
            in = ParameterIn.HEADER
    )
    public void login() {
    }
}
