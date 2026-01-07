package com.example.nexus.app.participation.controller.doc;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.participation.controller.dto.request.ParticipationApplicationRequest;
import com.example.nexus.app.participation.controller.dto.request.ParticipantSearchRequest;
import com.example.nexus.app.participation.controller.dto.response.ParticipantDetailResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipantPrivacyResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipantListResponse;
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
                  - `FEEDBACK_COMPLETED`: 피드백 완료
                  - `TEST_COMPLETED`: 테스트 완료
                  - `REJECTED`: 거절됨

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
            summary = "참여자 테스트 완료 처리",
            description = """
                  모집자가 참여자의 피드백을 확인 후 최종 완료 처리합니다.
                  - FEEDBACK_COMPLETED(피드백 완료) 상태에서만 호출 가능
                  - TEST_COMPLETED(테스트 완료) 상태로 변경
                  - 리워드 지급 준비 완료
                  """
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
                - feedbackCompletedCount: 피드백 완료 인원
                - testCompletedCount: 테스트 완료 인원
                - paidCount: 리워드 지급 완료 인원
                - rejectedCount: 거절됨 인원
                - totalCount: 전체 신청 인원
                """
    )
    ResponseEntity<ApiResponse<ParticipationStatisticsResponse>> getPostApplicationStatistics(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "게시글 참여자 목록 조회",
            description = """
                특정 게시글의 참여자 목록 조회 (페이징, 필터링, 정렬, 검색 지원)

                **필터링 가능 항목:**
                - status (참여자 상태)
                  - `PENDING`: 승인 대기
                  - `APPROVED`: 진행중
                  - `FEEDBACK_COMPLETED`: 피드백 완료
                  - `TEST_COMPLETED`: 테스트 완료
                  - `REJECTED`: 거절됨
                  - null: 전체
                - searchKeyword: 닉네임 또는 이메일 검색

                **정렬:**
                - sortDirection: `ASC` (오래된순), `DESC` (최신순, 기본값)
                - 항상 신청일시(appliedAt) 기준으로 정렬
                """
    )
    ResponseEntity<ApiResponse<Page<ParticipantListResponse>>> getParticipants(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            ParticipantSearchRequest searchRequest,
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "참여자 상세 정보 조회",
            description = "특정 참여자의 상세 정보를 조회합니다. (게시글 작성자만 가능)"
    )
    ResponseEntity<ApiResponse<ParticipantDetailResponse>> getParticipantDetail(
            @Parameter(description = "참여 ID", required = true)
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "참여자 피드백 완료 처리",
            description = """
                참여자 본인이 피드백 제출을 완료 처리합니다.
                - APPROVED(진행중) 상태에서만 호출 가능
                - FEEDBACK_COMPLETED(피드백 완료) 상태로 변경
                - 피드백 제출 후 호출해야 함
                """
    )
    ResponseEntity<ApiResponse<Void>> completeTestByParticipant(
            @Parameter(description = "참가 신청 ID", required = true)
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
