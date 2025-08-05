package com.example.nexus.app.recruitment.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.recruitment.controller.dto.request.RecruitmentProfileUpdateRequest;
import com.example.nexus.app.recruitment.controller.dto.response.RecruitmentProfileResponse;
import com.example.nexus.app.recruitment.service.RecruitmentProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "모집자 프로필", description = "모집자 프로필 관리 API")
@RestController
@RequestMapping("/v1/users/profile")
@RequiredArgsConstructor
public class RecruitmentProfileController {

    private final RecruitmentProfileService recruitmentProfileService;

    @Operation(summary = "프로필 조회", description = "현재 로그인한 모집자의 프로필 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<RecruitmentProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        RecruitmentProfileResponse response = recruitmentProfileService.getMyProfile(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "모집자 프로필 수정", description = "닉네임, 호스트 소개글,프로필 이미지를 수정합니다.")
    @PatchMapping(value = "/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RecruitmentProfileResponse>> updateCompleteProfile(
            @RequestPart("profile") @Valid RecruitmentProfileUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        RecruitmentProfileResponse response = recruitmentProfileService.updateCompleteProfile(userDetails.getUserId(), request, image);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
