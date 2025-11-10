package com.example.nexus.app.feedback.controller.doc;

import com.example.nexus.app.feedback.controller.dto.request.FeedbackDraftRequest;
import com.example.nexus.app.feedback.controller.dto.request.FeedbackSubmitRequest;
import com.example.nexus.app.feedback.controller.dto.request.PresignedUrlRequest;
import com.example.nexus.app.feedback.controller.dto.response.FeedbackDraftResponse;
import com.example.nexus.app.feedback.controller.dto.response.FeedbackResponse;
import com.example.nexus.app.feedback.controller.dto.response.MyFeedbackStatusResponse;
import com.example.nexus.app.feedback.controller.dto.response.PresignedUrlResponse;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface FeedbackControllerDoc {

    @Operation(
            summary = "피드백 임시저장",
            description = "참여자가 작성 중인 피드백을 임시로 저장합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "임시저장 성공",
                    content = @Content(schema = @Schema(implementation = FeedbackDraftResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    ResponseEntity<ApiResponse<FeedbackDraftResponse>> saveDraft(
            @Valid @RequestBody FeedbackDraftRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "피드백 최종 제출",
            description = "참여자가 피드백을 최종 제출합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "피드백 제출 성공",
                    content = @Content(schema = @Schema(implementation = FeedbackResponse.class))
            )
    })
    ResponseEntity<ApiResponse<FeedbackResponse>> submitFeedback(
            @Valid @RequestBody FeedbackSubmitRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "스크린샷 업로드용 Presigned URL 생성",
            description = "피드백에 첨부할 스크린샷을 업로드하기 위한 S3 Presigned URL을 생성합니다."
    )
    ResponseEntity<ApiResponse<PresignedUrlResponse>> generatePresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    );

    @Operation(
            summary = "내 피드백 상태 조회",
            description = "특정 프로젝트에 대한 내 피드백 상태를 조회합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    ResponseEntity<ApiResponse<MyFeedbackStatusResponse>> getMyFeedbackStatus(
            @Parameter(description = "프로젝트(게시글) ID") @RequestParam Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "피드백 단건 조회",
            description = "피드백 ID로 피드백을 조회합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    ResponseEntity<ApiResponse<FeedbackResponse>> getFeedback(
            @Parameter(description = "피드백 ID") @PathVariable Long feedbackId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );
}

