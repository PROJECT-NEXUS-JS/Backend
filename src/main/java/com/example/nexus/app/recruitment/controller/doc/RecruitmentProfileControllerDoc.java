package com.example.nexus.app.recruitment.controller.doc;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.recruitment.controller.dto.request.RecruitmentProfileUpdateRequest;
import com.example.nexus.app.recruitment.controller.dto.response.RecruitmentProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "모집자 프로필", description = "모집자 프로필 관리 API")
public interface RecruitmentProfileControllerDoc {

    @Operation(
            summary = "프로필 조회",
            description = "현재 로그인한 모집자의 프로필 정보를 조회합니다."
    )
    ResponseEntity<ApiResponse<RecruitmentProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "모집자 프로필 수정",
            description = """
                      닉네임, 호스트 소개글, 프로필 이미지를 수정합니다.

                      - 닉네임과 호스트 소개글은 필수 항목입니다.
                      - 프로필 이미지는 선택 사항이며, 제공하지 않을 경우 기존 이미지가 유지됩니다.
                      """
    )
    ResponseEntity<ApiResponse<RecruitmentProfileResponse>> updateCompleteProfile(
            @Valid @RequestPart("profile") RecruitmentProfileUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
