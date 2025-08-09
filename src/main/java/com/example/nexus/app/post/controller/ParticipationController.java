package com.example.nexus.app.post.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.post.controller.dto.request.ParticipationApplicationRequest;
import com.example.nexus.app.post.controller.dto.response.ParticipantPrivacyResponse;
import com.example.nexus.app.post.controller.dto.response.ParticipationResponse;
import com.example.nexus.app.post.domain.ParticipationStatus;
import com.example.nexus.app.post.service.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "참가 신청", description = "게시글 참가 신청 관련 API")
@RestController
@RequestMapping("/v1/users/posts")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    @Operation(summary = "참가 신청", description = "게시글에 참가 신청을 합니다.")
    @PostMapping("/{postId}/apply")
    public ResponseEntity<ApiResponse<ParticipationResponse>> applyForParticipation(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @Valid @RequestBody ParticipationApplicationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ParticipationResponse response = participationService.applyForParticipation(postId, userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "내 신청 내역 조회 (전체)", description = "사용자의 모든 참가 신청내역을 조회합니다.")
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getMyApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20)Pageable pageable) {

        Page<ParticipationResponse> applications = participationService.getMyApplications(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(applications));
    }

    @Operation(summary = "내 신청 내역 조회 (상태별)", description = "특정 상태의 참가 신청 내역을 조회합니다.")
    @GetMapping("/applications/{status}")
    public ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getMyApplicationsByStatus(
            @Parameter(description = "참가 상태 (PENDING, APPROVED, REJECTED)", required = true)
            @PathVariable ParticipationStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ParticipationResponse> applications = participationService.getMyApplications(userDetails.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(applications));
    }

    @Operation(summary = "참가 신청 취소", description = "참가 신청을 취소합니다. (신청자 본인만 가능)")
    @DeleteMapping("/applications/{participationId}")
    public ResponseEntity<ApiResponse<Void>> cancelApplication(
            @Parameter(description = "참가 신청 ID", required = true)
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        participationService.cancelApplication(participationId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "참가 신청 상태 확인", description = "특정 게시글에 대한 참가 신청여부를 확인합니다.")
    @GetMapping("/{postId}/apply/status")
    public ResponseEntity<ApiResponse<Boolean>> getApplicationStatus(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean hasApplied = participationService.hasApplied(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(hasApplied));
    }

    @Operation(summary = "게시글 신청자 목록 조회 (전체)", description = "게시글의 모든 신청자 목록을 조회합니다. (작성자만 가능)")
    @GetMapping("/{postId}/applications")
    public ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getPostApplications(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ParticipationResponse> applications = participationService.getPostApplications(postId, userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(applications));
    }

    @Operation(summary = "게시글 신청자 목록 조회 (상태별)", description = "게시글의 특정 상태 신청자 목록을 조회합니다. (작성자만 가능)")
    @GetMapping("/{postId}/applications/{status}")
    public ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getPostApplicationsByStatus(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @Parameter(description = "참가 상태 (PENDING, APPROVED, REJECTED)", required = true)
            @PathVariable ParticipationStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ParticipationResponse> applications = participationService.getPostApplications(postId, userDetails.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(applications));
    }

    @Operation(summary = "참가 신청 승인", description = "참가 신청을 승인합니다. (게시글 작성자만 가능)")
    @PatchMapping("/applications/{participationId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveApplication(
            @Parameter(description = "참가 신청 ID", required = true)
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        participationService.approveApplication(participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "참가 신청 거절", description = "참가 신청을 거절합니다. (게시글 작성자만 가능)")
    @PatchMapping("/applications/{participationId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectApplication(
            @Parameter(description = "참가 신청 ID", required = true)
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        participationService.rejectApplication(participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "게시글 신청자 개인정보 조회", description = "게시글 작성자가 신청자들의 개인정보를 조회합니다 (페이징)")
    @GetMapping("/{postId}/participants/privacy")
    public ResponseEntity<ApiResponse<Page<ParticipantPrivacyResponse>>>
    getParticipantsPrivacyInfo(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @PageableDefault(size = 20, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<ParticipantPrivacyResponse> response = participationService.getParticipantsPrivacyInfo(postId, pageable, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
