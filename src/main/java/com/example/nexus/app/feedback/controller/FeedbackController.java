package com.example.nexus.app.feedback.controller;

import com.example.nexus.app.feedback.controller.doc.FeedbackControllerDoc;
import com.example.nexus.app.feedback.controller.dto.request.FeedbackDraftRequest;
import com.example.nexus.app.feedback.controller.dto.request.FeedbackSubmitRequest;
import com.example.nexus.app.feedback.controller.dto.request.PresignedUrlRequest;
import com.example.nexus.app.feedback.controller.dto.response.FeedbackDraftResponse;
import com.example.nexus.app.feedback.controller.dto.response.FeedbackResponse;
import com.example.nexus.app.feedback.controller.dto.response.MyFeedbackStatusResponse;
import com.example.nexus.app.feedback.controller.dto.response.PresignedUrlResponse;
import com.example.nexus.app.feedback.service.FeedbackService;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "피드백", description = "피드백 관련 API")
@RestController
@RequestMapping("/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackController implements FeedbackControllerDoc {

    private final FeedbackService feedbackService;

    @Override
    @PostMapping("/draft")
    public ResponseEntity<ApiResponse<FeedbackDraftResponse>> saveDraft(
            @Valid @RequestBody FeedbackDraftRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        FeedbackDraftResponse response = feedbackService.saveDraft(request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackResponse>> submitFeedback(
            @Valid @RequestBody FeedbackSubmitRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        FeedbackResponse response = feedbackService.submitFeedback(request, userDetails.getUserId());
        return ResponseEntity.status(201).body(ApiResponse.onSuccess(response));
    }

    @Override
    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> generatePresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        PresignedUrlResponse response = feedbackService.generatePresignedUrl(
                request.fileName(),
                request.contentType()
        );
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/my-status")
    public ResponseEntity<ApiResponse<MyFeedbackStatusResponse>> getMyFeedbackStatus(
            @RequestParam Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            MyFeedbackStatusResponse response = MyFeedbackStatusResponse.notStarted(null);
            return ResponseEntity.ok(ApiResponse.onSuccess(response));
        }

        MyFeedbackStatusResponse response = feedbackService.getMyFeedbackStatus(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<FeedbackResponse>> getFeedback(
            @PathVariable Long feedbackId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        FeedbackResponse response = feedbackService.getFeedback(feedbackId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}

