package com.example.nexus.app.account.controller;

import com.example.nexus.app.account.dto.AccountManagementResponse;
import com.example.nexus.app.account.dto.BasicInfoUpdateRequest;
import com.example.nexus.app.account.dto.CustomInfoUpdateRequest;
import com.example.nexus.app.account.dto.PersonalInfoUpdateRequest;
import com.example.nexus.app.account.dto.AccountWithdrawalRequest;
import com.example.nexus.app.account.dto.KakaoAccountInfoResponse;
import com.example.nexus.app.account.service.AccountManagementService;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "계정관리", description = "계정관리 API")
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountManagementController {

    private final AccountManagementService accountManagementService;

    @Operation(summary = "계정관리 정보 조회", description = "현재 로그인한 사용자의 계정관리 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<AccountManagementResponse>> getAccountManagementInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AccountManagementResponse response = accountManagementService.getAccountManagementInfo(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "카카오 계정 정보 조회", description = "현재 로그인한 사용자의 카카오 계정 연결 정보를 조회합니다.")
    @GetMapping("/kakao")
    public ResponseEntity<ApiResponse<KakaoAccountInfoResponse>> getKakaoAccountInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        KakaoAccountInfoResponse response = accountManagementService.getKakaoAccountInfo(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "기본정보 수정", description = "활동명과 프로필 이미지를 수정합니다.")
    @PutMapping(value = "/basic-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AccountManagementResponse>> updateBasicInfo(
            @RequestPart("basicInfo") @Valid BasicInfoUpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AccountManagementResponse response = accountManagementService.updateBasicInfo(userDetails.getUserId(), request, profileImage);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "개인정보 수정", description = "전화번호를 수정합니다.")
    @PutMapping("/personal-info")
    public ResponseEntity<ApiResponse<AccountManagementResponse>> updatePersonalInfo(
            @Valid @RequestBody PersonalInfoUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AccountManagementResponse response = accountManagementService.updatePersonalInfo(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "맞춤정보 수정", description = "직업, 출생년도, 성별, 관심사, 선호 장르를 수정합니다.")
    @PutMapping("/custom-info")
    public ResponseEntity<ApiResponse<AccountManagementResponse>> updateCustomInfo(
            @Valid @RequestBody CustomInfoUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AccountManagementResponse response = accountManagementService.updateCustomInfo(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        accountManagementService.logout(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "계정 탈퇴", description = "현재 사용자의 계정을 탈퇴 처리합니다.")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawAccount(
            @Valid @RequestBody AccountWithdrawalRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        accountManagementService.withdrawAccount(userDetails.getUserId(), request.confirmation(), request.kakaoAccessToken());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
