package com.example.nexus.app.participation.controller.doc;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import com.example.nexus.app.participation.dto.request.ParticipationApplicationRequest;
import com.example.nexus.app.participation.dto.response.ParticipantPrivacyResponse;
import com.example.nexus.app.participation.dto.response.ParticipationResponse;
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
            summary = "내 신청 내역 조회 (전체)",
            description = "사용자의 모든 참가 신청내역을 조회합니다."
    )
    ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getMyApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    );

    @Operation(
            summary = "내 신청 내역 조회 (상태별)",
            description = """
                      특정 상태의 참가 신청 내역을 조회합니다.

                      **ParticipationStatus (참가 신청 상태):**
                      - `PENDING`: 대기중
                      - `APPROVED`: 승인됨
                      - `COMPLETED`: 테스트 완료
                      - `REJECTED`: 거절됨
                      """
    )
    ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getMyApplicationsByStatus(
            @Parameter(
                    description = "참가 상태",
                    required = true,
                    schema = @Schema(implementation = ParticipationStatus.class)
            )
            @PathVariable ParticipationStatus status,
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
            summary = "게시글 신청자 목록 조회 (전체)",
            description = "게시글의 모든 신청자 목록을 조회합니다. (작성자만 가능)"
    )
    ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getPostApplications(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    );

    @Operation(
            summary = "게시글 신청자 목록 조회 (상태별)",
            description = """
                      게시글의 특정 상태 신청자 목록을 조회합니다. (작성자만 가능)

                      **ParticipationStatus (참가 신청 상태):**
                      - `PENDING`: 대기중
                      - `APPROVED`: 승인됨
                      - `COMPLETED`: 테스트 완료
                      - `REJECTED`: 거절됨
                      """
    )
    ResponseEntity<ApiResponse<Page<ParticipationResponse>>> getPostApplicationsByStatus(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @Parameter(
                    description = "참가 상태",
                    required = true,
                    schema = @Schema(implementation = ParticipationStatus.class)
            )
            @PathVariable ParticipationStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
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
}
