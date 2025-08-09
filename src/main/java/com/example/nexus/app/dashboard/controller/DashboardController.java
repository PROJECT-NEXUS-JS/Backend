package com.example.nexus.app.dashboard.controller;

import com.example.nexus.app.dashboard.controller.dto.request.ParticipantSearchRequest;
import com.example.nexus.app.dashboard.controller.dto.response.*;
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

@Tag(name = "모집자 대시보드", description = "모집자 대시보드 API")
@RestController
@RequestMapping("/v1/users/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "게시글 참여자 목록 조회 (리워드 지급용)", description = "특정게시글의 참여자 목록 조회 (페이징, 필터링, 정렬, 검색 지원)")
    @GetMapping("/{postId}/participants")
    public ResponseEntity<ApiResponse<Page<ParticipantListResponse>>> getParticipants(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            ParticipantSearchRequest searchRequest,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<ParticipantListResponse> response = dashboardService.getParticipants(postId, searchRequest, pageable, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "참여자 상세 조회", description = "특정 참여자의 상세 정보 조회")
    @GetMapping("/{postId}/participants/{participationId}")
    public ResponseEntity<ApiResponse<ParticipantDetailResponse>> getParticipantDetail(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @PathVariable @Schema(description = "참여 ID") Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ParticipantDetailResponse response = dashboardService.getParticipantDetail(postId, participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "참여자 완료 처리", description = "특정 참여자의 테스트를 완료 처리합니다")
    @PostMapping("/{postId}/participants/{participationId}/complete")
    public ResponseEntity<ApiResponse<ParticipantDetailResponse>> completeParticipant(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @PathVariable @Schema(description = "참여 ID") Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ParticipantDetailResponse response = dashboardService.completeParticipant(postId, participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "리워드 지급 처리", description = "특정 참여자에게 리워드를 지급 처리합니다")
    @PostMapping("/{postId}/participants/{participationId}/reward")
    public ResponseEntity<ApiResponse<ParticipantDetailResponse>> payReward(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @PathVariable @Schema(description = "참여 ID") Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ParticipantDetailResponse response = dashboardService.payReward(postId, participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "대시보드 통계 카드", description = "특정 게시글의 좋아요, 찜하기,참여 대기, 참여자, 리뷰, 조회수 통계, 안 읽은 메시지 (전일 대비 증감 포함)")
    @GetMapping("/{postId}/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DashboardStatsResponse response = dashboardService.getDashboardStats(userDetails.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "빠른 액션 - 참여 대기", description = "특정 게시글의 참여 대기 목록 조회")
    @GetMapping("/{postId}/quick-actions/waiting")
    public ResponseEntity<ApiResponse<Page<WaitingParticipantResponse>>>
    getWaitingParticipants(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 2, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<WaitingParticipantResponse> response = dashboardService.getWaitingParticipants(userDetails.getUserId(), postId, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "빠른 액션 - 메시지", description = "특정 게시글 관련 최근 메시지 목록 조회")
    @GetMapping("/{postId}/quick-actions/messages")
    public ResponseEntity<ApiResponse<Page<RecentMessageResponse>>> getRecentMessages(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RecentMessageResponse> response = dashboardService.getRecentMessages(userDetails.getUserId(), postId, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "빠른 액션 - 최근 리뷰", description = "특정 게시글 관련 최근 리뷰 목록 조회")
    @GetMapping("/{postId}/quick-actions/reviews")
    public ResponseEntity<ApiResponse<Page<RecentReviewResponse>>> getRecentReviews(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RecentReviewResponse> response = dashboardService.getRecentReviews(userDetails.getUserId(), postId, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "모집 상태 변경", description = "모집중 <-> 모집 완료 상태 토글")
    @PostMapping("/{postId}/recruitment-status")
    public ResponseEntity<ApiResponse<PostStatusResponse>> toggleRecruitmentStatus(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostStatusResponse response = dashboardService.toggleRecruitmentStatus(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "분석 그래프 - 막대 차트", description = "특정 게시글의 지난 7일간 좋아요, 찜하기, 신청자, 참여자, 리뷰 데이터")
    @GetMapping("/{postId}/analytics/bar-chart")
    public ResponseEntity<ApiResponse<BarChartResponse>> getBarChartData(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        BarChartResponse response = dashboardService.getBarChartData(userDetails.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "분석 그래프 - 원형 차트", description = "특정 게시글의 대기/진행/완료 비율 및 리워드 지급/미지급 비율")
    @GetMapping("/{postId}/analytics/pie-chart")
    public ResponseEntity<ApiResponse<PieChartResponse>> getPieChartData(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PieChartResponse response = dashboardService.getPieChartData(userDetails.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "분석 그래프 - 선형 차트", description = "특정 게시글의 지난 7일간 일별 좋아요, 신청, 찜하기 추이")
    @GetMapping("/{postId}/analytics/line-chart")
    public ResponseEntity<ApiResponse<LineChartResponse>> getLineChartData(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LineChartResponse response = dashboardService.getLineChartData(userDetails.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "내 게시글 목록", description = "대시보드에서 선택할 수 있는 내 게시글 목록 조회")
    @GetMapping("/my-posts")
    public ResponseEntity<ApiResponse<Page<MyPostSummaryResponse>>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MyPostSummaryResponse> response = dashboardService.getMyPosts(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
