package com.example.nexus.app.participation.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.participation.controller.doc.ParticipationControllerDoc;
import com.example.nexus.app.participation.controller.dto.request.ParticipantSearchRequest;
import com.example.nexus.app.participation.controller.dto.request.ParticipationApplicationRequest;
import com.example.nexus.app.participation.controller.dto.response.ParticipantDetailResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipantListResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipantPrivacyResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipationResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipationStatisticsResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipationSummaryResponse;
import com.example.nexus.app.participation.service.ParticipationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users/participations")
@RequiredArgsConstructor
public class ParticipationController implements ParticipationControllerDoc {

    private final ParticipationService participationService;

    @Override
    @PostMapping("/posts/{postId}/apply")
    public ResponseEntity<ApiResponse<ParticipationResponse>> applyForParticipation(
            @PathVariable Long postId,
            @Valid @RequestBody ParticipationApplicationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ParticipationResponse response = participationService.applyForParticipation(postId, userDetails.getUserId(),
                request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ParticipationSummaryResponse>>> getMyApplications(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ParticipationSummaryResponse> applications = participationService.getMyApplications(
                userDetails.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(applications));
    }

    @Override
    @DeleteMapping("/{participationId}")
    public ResponseEntity<ApiResponse<Void>> cancelApplication(
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        participationService.cancelApplication(participationId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.onSuccess(null));
    }

    @Override
    @GetMapping("/posts/{postId}/status")
    public ResponseEntity<ApiResponse<Boolean>> getApplicationStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean hasApplied = participationService.hasApplied(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(hasApplied));
    }

    @Override
    @PatchMapping("/{participationId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveApplication(
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        participationService.approveApplication(participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Override
    @PatchMapping("/{participationId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectApplication(
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        participationService.rejectApplication(participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Override
    @GetMapping("/posts/{postId}/privacy")
    public ResponseEntity<ApiResponse<Page<ParticipantPrivacyResponse>>> getParticipantsPrivacyInfo(
            @PathVariable Long postId,
            @PageableDefault(size = 20, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<ParticipantPrivacyResponse> response = participationService.getParticipantsPrivacyInfo(postId, pageable,
                userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @PatchMapping("/{participationId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeParticipant(
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        participationService.completeParticipant(participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Override
    @GetMapping("/posts/{postId}/statistics")
    public ResponseEntity<ApiResponse<ParticipationStatisticsResponse>> getPostApplicationStatistics(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ParticipationStatisticsResponse statistics = participationService.getPostApplicationStatistics(
                postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(statistics));
    }

    @Override
    @GetMapping("/posts/{postId}/participants")
    public ResponseEntity<ApiResponse<Page<ParticipantListResponse>>> getParticipants(
            @PathVariable Long postId,
            ParticipantSearchRequest searchRequest,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<ParticipantListResponse> response = participationService.getParticipants(
                postId, searchRequest, pageable, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{participationId}")
    public ResponseEntity<ApiResponse<ParticipantDetailResponse>> getParticipantDetail(
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ParticipantDetailResponse response = participationService.getParticipantDetail(
                participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
