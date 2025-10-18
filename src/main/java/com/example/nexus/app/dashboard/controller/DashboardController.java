package com.example.nexus.app.dashboard.controller;

import com.example.nexus.app.dashboard.controller.dto.request.ParticipantSearchRequest;
import com.example.nexus.app.dashboard.controller.dto.response.*;
import com.example.nexus.app.dashboard.doc.DashboardControllerDoc;
import com.example.nexus.app.dashboard.service.DashboardService;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users/dashboard")
@RequiredArgsConstructor
public class DashboardController implements DashboardControllerDoc {

    private final DashboardService dashboardService;

    @Override
    @GetMapping("/{postId}/participants")
    public ResponseEntity<ApiResponse<Page<ParticipantListResponse>>> getParticipants(
            @PathVariable Long postId,
            ParticipantSearchRequest searchRequest,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<ParticipantListResponse> response = dashboardService.getParticipants(postId, searchRequest, pageable, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{postId}/participants/{participationId}")
    public ResponseEntity<ApiResponse<ParticipantDetailResponse>> getParticipantDetail(
            @PathVariable Long postId,
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ParticipantDetailResponse response = dashboardService.getParticipantDetail(postId, participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @PostMapping("/{postId}/participants/{participationId}/complete")
    public ResponseEntity<ApiResponse<ParticipantDetailResponse>> completeParticipant(
            @PathVariable Long postId,
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ParticipantDetailResponse response = dashboardService.completeParticipant(postId, participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @PostMapping("/{postId}/participants/{participationId}/reward")
    public ResponseEntity<ApiResponse<ParticipantDetailResponse>> payReward(
            @PathVariable Long postId,
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ParticipantDetailResponse response = dashboardService.payReward(postId, participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{postId}/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DashboardStatsResponse response = dashboardService.getDashboardStats(userDetails.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{postId}/quick-actions/waiting")
    public ResponseEntity<ApiResponse<Page<WaitingParticipantResponse>>> getWaitingParticipants(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 2, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<WaitingParticipantResponse> response = dashboardService.getWaitingParticipants(userDetails.getUserId(), postId, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{postId}/quick-actions/messages")
    public ResponseEntity<ApiResponse<Page<RecentMessageResponse>>> getRecentMessages(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RecentMessageResponse> response = dashboardService.getRecentMessages(userDetails.getUserId(), postId, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{postId}/quick-actions/reviews")
    public ResponseEntity<ApiResponse<Page<RecentReviewResponse>>> getRecentReviews(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RecentReviewResponse> response = dashboardService.getRecentReviews(userDetails.getUserId(), postId, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @PostMapping("/{postId}/recruitment-status")
    public ResponseEntity<ApiResponse<PostStatusResponse>> toggleRecruitmentStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostStatusResponse response = dashboardService.toggleRecruitmentStatus(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{postId}/analytics/bar-chart")
    public ResponseEntity<ApiResponse<BarChartResponse>> getBarChartData(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        BarChartResponse response = dashboardService.getBarChartData(userDetails.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{postId}/analytics/pie-chart")
    public ResponseEntity<ApiResponse<PieChartResponse>> getPieChartData(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PieChartResponse response = dashboardService.getPieChartData(userDetails.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{postId}/analytics/line-chart")
    public ResponseEntity<ApiResponse<LineChartResponse>> getLineChartData(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LineChartResponse response = dashboardService.getLineChartData(userDetails.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/my-posts")
    public ResponseEntity<ApiResponse<Page<MyPostSummaryResponse>>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MyPostSummaryResponse> response = dashboardService.getMyPosts(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
