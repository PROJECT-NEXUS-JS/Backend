package com.example.nexus.app.participation.controller.doc;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.participation.controller.dto.request.ParticipationApplicationRequest;
import com.example.nexus.app.participation.controller.dto.response.ParticipantPrivacyResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipationResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipationStatisticsResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipationSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "참가 신청", description = "게시글 참가 신청 관련 API")
public interface ParticipationControllerDoc {

    @Operation(
            summary = "참가 신청",
            description = "게시글에 참가 신청을 합니다."
    )
    ResponseEntity<ApiResponse<ParticipationResponse>> applyForParticipation(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @Valid @RequestBody ParticipationApplicationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "내 신청 내역 조회",
            description = """
                    내 참가 신청 내역을 조회합니다. status 파라미터로 필터링 가능
                    
                    **ParticipationStatus (참가 신청 상태):**
                    - `PENDING`: 승인 대기
                    - `APPROVED`: 진행중
                    - `COMPLETED`: 지급 대기
                    - `REJECTED`: 거절됨
                    - `PAID`: 완료 (지급완료)
                    
                    status를 입력하지 않으면 전체 조회
                    """
    )
    ResponseEntity<ApiResponse<Page<ParticipationSummaryResponse>>> getMyApplications(
            @Parameter(description = "참가 상태 (선택사항)", required = false)
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    );

    @Operation(
            summary = "참가 신청 취소",
            description = "참가 신청을 취소합니다. (신청자 본인만 가능)"
    )
    ResponseEntity<ApiResponse<Void>> cancelApplication(
            @Parameter(description = "참가 신청 ID", required = true)
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "참가 신청 상태 확인",
            description = "특정 게시글에 대한 참가 신청여부를 확인합니다."
    )
    ResponseEntity<ApiResponse<Boolean>> getApplicationStatus(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "게시글의 참가 신청자 조회",
            description = """
                      게시글의 특정 상태 신청자 목록을 조회합니다. (작성자만 가능)
                    
                    **ParticipationStatus (참가 신청 상태):**
                    - `PENDING`: 승인 대기
                    - `APPROVED`: 진행중
                    - `COMPLETED`: 지급 대기
                    - `REJECTED`: 거절됨
                    - `PAID`: 완료 (지급완료)
                    
                    status를 입력하지 않으면 전체 조회
                    """)
    ResponseEntity<ApiResponse<Page<ParticipationSummaryResponse>>> getPostApplications(
            @PathVariable Long postId,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable);

    @Operation(
            summary = "참가 신청 승인",
            description = "참가 신청을 승인합니다. (게시글 작성자만 가능)"
    )
    ResponseEntity<ApiResponse<Void>> approveApplication(
            @Parameter(description = "참가 신청 ID", required = true)
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "참가 신청 거절",
            description = "참가 신청을 거절합니다. (게시글 작성자만 가능)"
    )
    ResponseEntity<ApiResponse<Void>> rejectApplication(
            @Parameter(description = "참가 신청 ID", required = true)
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "게시글 신청자 개인정보 조회",
            description = "게시글 작성자가 신청자들의 개인정보를 조회합니다 (페이징)"
    )
    ResponseEntity<ApiResponse<Page<ParticipantPrivacyResponse>>> getParticipantsPrivacyInfo(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "참여자 완료 처리",
            description = "특정 참여자의 테스트를 완료 처리합니다 (게시글 작성자만 가능)"
    )
    ResponseEntity<ApiResponse<Void>> completeParticipant(
            @Parameter(description = "참여 ID", required = true)
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "게시글 참가 신청 통계",
            description = """
                  게시글의 상태별 참가 신청자 인원 통계를 조회합니다. (작성자만 가능)

                  - pendingCount: 승인 대기 인원
                  - approvedCount: 진행중 인원
                  - completedCount: 완료 (지급 대기) 인원
                  - paidCount: 지급 완료 인원
                  - rejectedCount: 거절됨 인원
                  - totalCount: 전체 신청 인원
                  """
    )
    ResponseEntity<ApiResponse<ParticipationStatisticsResponse>> getPostApplicationStatistics(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
