package com.example.nexus.app.user.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.dto.LoginResponseDto;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.user.dto.ProfileRequestDto;
import com.example.nexus.app.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "User OIDC Login", description = "OIDC Login API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "프로필(직업, 관심사) 등록 또는 수정", description = "요청자의 Role이 GUEST인 경우 USER로 역할변경(정회원 처리, 토큰 재발급), USER인 경우 정보만 수정(기존 토큰 유지)합니다.")
    @PutMapping("/profile")
    public ApiResponse<LoginResponseDto> updateOrCompleteProfile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProfileRequestDto requestDto) {

        String accessToken = authorizationHeader.substring(7);

        Optional<LoginResponseDto> optionalTokens = userProfileService.updateOrCompleteProfile(userDetails.getUserId(), accessToken, requestDto);

        return ApiResponse.onSuccess(optionalTokens.orElse(null));
    }
}
