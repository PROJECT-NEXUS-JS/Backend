package com.example.nexus.app.global.oauth.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.dto.LoginResponseDto;
import com.example.nexus.app.global.code.dto.TokenReissueResponseDto;
import com.example.nexus.app.global.code.dto.UserInfoResponseDto;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.oauth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User OIDC Login", description = "OIDC Login API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "OIDC 로그인", description = "카카오에서 받은 id_token으로 로그인/회원가입 처리 후 우리 서비스의 토큰을 발급합니다.")
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestHeader("id_token") String idToken) {
        LoginResponseDto tokens = authService.login(idToken);
        return ApiResponse.onSuccess(tokens);
    }

    @Operation(summary = "내 정보 조회", description = "로그인된 사용자의 정보를 조회합니다. (AccessToken 필요)")
    @GetMapping("/me")
    public ApiResponse<UserInfoResponseDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ApiResponse.onFailure("AUTH401", "인증 정보가 없습니다.", null);
        }
        UserInfoResponseDto userInfo = authService.getUserInfo(userDetails.getUserId());
        return ApiResponse.onSuccess(userInfo);
    }

    @Operation(summary = "Access Token 재발급", description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
    @PostMapping("/reissue")
    public ApiResponse<TokenReissueResponseDto> reissueToken(
            @Parameter(description = "Refresh Token (Bearer 스키마 제외)", required = true)
            @RequestHeader(name = "${jwt.refresh.header}") String refreshToken) {
        String newAccessToken = authService.reissueAccessToken(refreshToken);
        return ApiResponse.onSuccess(new TokenReissueResponseDto(newAccessToken));
    }
}
