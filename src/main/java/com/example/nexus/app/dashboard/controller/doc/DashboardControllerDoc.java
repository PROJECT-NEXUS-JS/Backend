package com.example.nexus.app.dashboard.controller.doc;

import com.example.nexus.app.dashboard.controller.dto.response.*;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "모집자 대시보드", description = "모집자 대시보드 API")
public interface DashboardControllerDoc {

    @Operation(
            summary = "대시보드 통계 카드",
            description = "특정 게시글의 좋아요, 찜하기, 참여 대기, 참여자, 리뷰, 조회수 통계, 리워드 지급대기 통계, 안 읽은 메시지 (전일 대비 증감 포함)"
    )
    ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "빠른 액션 - 참여 대기",
            description = "특정 게시글의 참여 대기 목록 조회"
    )
    ResponseEntity<ApiResponse<Page<WaitingParticipantResponse>>> getWaitingParticipants(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    );

    @Operation(
            summary = "빠른 액션 - 메시지",
            description = "특정 게시글 관련 최근 메시지 목록 조회"
    )
    ResponseEntity<ApiResponse<Page<RecentMessageResponse>>> getRecentMessages(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    );

    @Operation(
            summary = "빠른 액션 - 최근 리뷰",
            description = "특정 게시글 관련 최근 리뷰 목록 조회"
    )
    ResponseEntity<ApiResponse<Page<RecentReviewResponse>>> getRecentReviews(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    );

    @Operation(
            summary = "모집 상태 변경",
            description = """
                    모집중 <-> 모집 완료 상태 토글
                    
                    **PostStatus (게시글 상태):**
                    - `ACTIVE`: 활성 (모집중)
                    - `COMPLETED`: 완료 (모집 완료)
                    """
    )
    ResponseEntity<ApiResponse<PostStatusResponse>> toggleRecruitmentStatus(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "분석 그래프 - 막대 차트",
            description = "특정 게시글의 지난 7일간 좋아요, 찜하기, 신청자, 참여자, 리뷰 데이터"
    )
    ResponseEntity<ApiResponse<BarChartResponse>> getBarChartData(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "분석 그래프 - 원형 차트",
            description = """
                    특정 게시글의 대기/진행/완료 비율 및 리워드 지급/미지급 비율
                    
                    **ParticipationStatus 비율:**
                    - `PENDING`: 대기중
                    - `APPROVED`: 승인됨 (진행중)
                    - `COMPLETED`: 테스트 완료
                    
                    **RewardStatus 비율:**
                    - `PENDING`: 지급 대기
                    - `PAID`: 지급 완료
                    """
    )
    ResponseEntity<ApiResponse<PieChartResponse>> getPieChartData(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "분석 그래프 - 선형 차트",
            description = "특정 게시글의 지난 7일간 일별 좋아요, 신청, 찜하기 추이"
    )
    ResponseEntity<ApiResponse<LineChartResponse>> getLineChartData(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "내 게시글 목록",
            description = "대시보드에서 선택할 수 있는 내 게시글 목록 조회"
    )
    ResponseEntity<ApiResponse<Page<MyPostSummaryResponse>>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    );
}
