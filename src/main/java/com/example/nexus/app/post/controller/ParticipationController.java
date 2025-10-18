package com.example.nexus.app.post.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.post.controller.doc.ParticipationControllerDoc;
import com.example.nexus.app.post.controller.dto.request.ParticipationApplicationRequest;
import com.example.nexus.app.post.controller.dto.response.ParticipantPrivacyResponse;
import com.example.nexus.app.post.controller.dto.response.ParticipationResponse;
import com.example.nexus.app.post.domain.ParticipationStatus;
import com.example.nexus.app.post.service.ParticipationService;
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

@RestController
@RequestMapping("/v1/users/posts")
@RequiredArgsConstructor
public class ParticipationController implements ParticipationControllerDoc {

    private final ParticipationService participationService;

    @Override
    @PostMapping("/{postId}/apply")
    public ResponseEntity<ApiResponse<ParticipationResponse>> applyForParticipation(
            @PathVariable Long postId,
            @Valid @RequestBody ParticipationApplicationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ParticipationResponse response = participationService.applyForParticipation(postId, userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getMyApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ParticipationResponse> applications = participationService.getMyApplications(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(applications));
    }

    @Override
    @GetMapping("/applications/{status}")
    public ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getMyApplicationsByStatus(
            @PathVariable ParticipationStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ParticipationResponse> applications = participationService.getMyApplications(userDetails.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(applications));
    }

    @Override
    @DeleteMapping("/applications/{participationId}")
    public ResponseEntity<ApiResponse<Void>> cancelApplication(
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        participationService.cancelApplication(participationId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.onSuccess(null));
    }

    @Override
    @GetMapping("/{postId}/apply/status")
    public ResponseEntity<ApiResponse<Boolean>> getApplicationStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean hasApplied = participationService.hasApplied(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(hasApplied));
    }

    @Override
    @GetMapping("/{postId}/applications")
    public ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getPostApplications(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ParticipationResponse> applications = participationService.getPostApplications(postId, userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(applications));
    }

    @Override
    @GetMapping("/{postId}/applications/{status}")
    public ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getPostApplicationsByStatus(
            @PathVariable Long postId,
            @PathVariable ParticipationStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ParticipationResponse> applications = participationService.getPostApplications(postId, userDetails.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(applications));
    }

    @Override
    @PatchMapping("/applications/{participationId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveApplication(
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        participationService.approveApplication(participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Override
    @PatchMapping("/applications/{participationId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectApplication(
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        participationService.rejectApplication(participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Override
    @GetMapping("/{postId}/participants/privacy")
    public ResponseEntity<ApiResponse<Page<ParticipantPrivacyResponse>>> getParticipantsPrivacyInfo(
            @PathVariable Long postId,
            @PageableDefault(size = 20, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<ParticipantPrivacyResponse> response = participationService.getParticipantsPrivacyInfo(postId, pageable, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
