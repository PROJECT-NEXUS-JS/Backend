package com.example.nexus.app.global.oauth.controller;

import com.example.nexus.app.account.dto.AccountManagementResponse;
import com.example.nexus.app.account.dto.BasicInfoUpdateRequest;
import com.example.nexus.app.account.dto.CustomInfoUpdateRequest;
import com.example.nexus.app.account.dto.PersonalInfoUpdateRequest;
import com.example.nexus.app.account.dto.KakaoAccountInfoResponse;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.dto.LoginResponseDto;
import com.example.nexus.app.global.code.dto.UserInfoResponseDto;
import com.example.nexus.app.global.code.dto.UserInfoUpdateRequest;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.oauth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = "내 정보 수정", description = "로그인된 사용자의 닉네임, 직업, 관심사 정보를 수정합니다. (AccessToken 필요)")
    @PutMapping("/me/edit")
    public ApiResponse<UserInfoResponseDto> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserInfoUpdateRequest request) {
        if (userDetails == null) {
            return ApiResponse.onFailure("AUTH401", "인증 정보가 없습니다.", null);
        }
        UserInfoResponseDto updatedUserInfo = authService.updateUserInfo(userDetails.getUserId(), request);
        return ApiResponse.onSuccess(updatedUserInfo);
    }

    @Operation(summary = "계정관리 정보 조회", description = "현재 로그인한 사용자의 계정관리 정보를 조회합니다.")
    @GetMapping("/account")
    public ResponseEntity<ApiResponse<AccountManagementResponse>> getAccountManagementInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AccountManagementResponse response = authService.getAccountManagementInfo(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "카카오 계정 정보 조회", description = "현재 로그인한 사용자의 카카오 계정 연결 정보를 조회합니다.")
    @GetMapping("/account/kakao")
    public ResponseEntity<ApiResponse<KakaoAccountInfoResponse>> getKakaoAccountInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        KakaoAccountInfoResponse response = authService.getKakaoAccountInfo(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "기본정보 수정", description = "활동명과 프로필 이미지를 수정합니다.")
    @PutMapping(value = "/account/basic-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AccountManagementResponse>> updateBasicInfo(
            @RequestPart("basicInfo") @Valid BasicInfoUpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AccountManagementResponse response = authService.updateBasicInfo(userDetails.getUserId(), request, profileImage);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "개인정보 수정", description = "전화번호를 수정합니다.")
    @PutMapping("/account/personal-info")
    public ResponseEntity<ApiResponse<AccountManagementResponse>> updatePersonalInfo(
            @Valid @RequestBody PersonalInfoUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AccountManagementResponse response = authService.updatePersonalInfo(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "맞춤정보 수정", description = "직업, 출생년도, 성별, 관심사, 선호 장르를 수정합니다.")
    @PutMapping("/account/custom-info")
    public ResponseEntity<ApiResponse<AccountManagementResponse>> updateCustomInfo(
            @Valid @RequestBody CustomInfoUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AccountManagementResponse response = authService.updateCustomInfo(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "계정 탈퇴", description = "현재 사용자의 계정을 탈퇴 처리합니다.")
    @DeleteMapping("/withdraw")
    public ApiResponse<Void> withdrawAccount(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String confirmation,
            @RequestParam(required = false) String kakaoAccessToken) {
        if (userDetails == null) {
            return ApiResponse.onFailure("AUTH401", "인증 정보가 없습니다.", null);
        }
        authService.withdrawAccount(userDetails.getUserId(), confirmation, kakaoAccessToken);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "Access/Refresh 토큰 재발급", description = "Refresh Token을 사용하여 새로운 Access/Refresh 토큰 쌍을 발급받고, 기존 토큰들은 무효화됩니다.")
    @PostMapping("/reissue")
    public ApiResponse<LoginResponseDto> reissueTokens(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @Parameter(description = "Refresh Token (Bearer 스키마 제외)", required = true)
            @RequestHeader("RefreshToken") String refreshToken) {

        String accessToken = authorizationHeader.substring(7);
        LoginResponseDto newTokens = authService.reissueTokens(accessToken, refreshToken);
        return ApiResponse.onSuccess(newTokens);
    }
}
