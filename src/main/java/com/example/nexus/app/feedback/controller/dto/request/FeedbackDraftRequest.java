package com.example.nexus.app.feedback.controller.dto.request;

import com.example.nexus.app.feedback.domain.BugType;
import com.example.nexus.app.feedback.domain.InconvenienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Schema(description = "피드백 임시저장 요청")
public record FeedbackDraftRequest(

        @Schema(description = "참여 ID", example = "1")
        @NotNull(message = "참여 ID는 필수입니다.")
        Long participationId,

        @Schema(description = "전반적인 만족도 (1-5)", example = "4")
        @Min(value = 1, message = "만족도는 1 이상이어야 합니다.")
        @Max(value = 5, message = "만족도는 5 이하여야 합니다.")
        Integer overallSatisfaction,

        @Schema(description = "추천 의향 (1-5)", example = "5")
        @Min(value = 1, message = "추천 의향은 1 이상이어야 합니다.")
        @Max(value = 5, message = "추천 의향은 5 이하여야 합니다.")
        Integer recommendationIntent,

        @Schema(description = "재이용 의향 (1-5)", example = "4")
        @Min(value = 1, message = "재이용 의향은 1 이상이어야 합니다.")
        @Max(value = 5, message = "재이용 의향은 5 이하여야 합니다.")
        Integer reuseIntent,

        @Schema(description = "가장 불편했던 부분", example = "UI_UX")
        InconvenienceType mostInconvenient,

        @Schema(description = "버그 존재 여부", example = "true")
        Boolean hasBug,

        @Schema(description = "발견된 문제 유형들")
        Set<BugType> bugTypes,

        @Schema(description = "문제 발생 위치", example = "로그인 페이지 - 입력 필드")
        @Size(max = 500, message = "문제 발생 위치는 500자 이하여야 합니다.")
        String bugLocation,

        @Schema(description = "버그 상황 설명")
        String bugDescription,

        @Schema(description = "첨부 스크린샷 URL 리스트")
        List<String> screenshotUrls,

        @Schema(description = "주요 기능 작동성 (1-5)", example = "4")
        @Min(value = 1, message = "기능 작동성은 1 이상이어야 합니다.")
        @Max(value = 5, message = "기능 작동성은 5 이하여야 합니다.")
        Integer functionalityScore,

        @Schema(description = "가이드 이해도 (1-5)", example = "5")
        @Min(value = 1, message = "가이드 이해도는 1 이상이어야 합니다.")
        @Max(value = 5, message = "가이드 이해도는 5 이하여야 합니다.")
        Integer comprehensibilityScore,

        @Schema(description = "로딩 속도 (1-5)", example = "3")
        @Min(value = 1, message = "로딩 속도는 1 이상이어야 합니다.")
        @Max(value = 5, message = "로딩 속도는 5 이하여야 합니다.")
        Integer speedScore,

        @Schema(description = "알림/반응 타이밍 (1-5)", example = "4")
        @Min(value = 1, message = "반응 타이밍은 1 이상이어야 합니다.")
        @Max(value = 5, message = "반응 타이밍은 5 이하여야 합니다.")
        Integer responseTimingScore,

        @Schema(description = "좋았던 점")
        String goodPoints,

        @Schema(description = "개선 제안")
        String improvementSuggestions,

        @Schema(description = "기타 자유 의견")
        String additionalComments
) {
}

