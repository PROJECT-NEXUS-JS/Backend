package com.example.nexus.app.post.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "게시글 추가 조건")
public record PostScreenerQuestionResponse(
        @Schema(description = "추가 조건", example = "[\"모바일 앱 사용 경험이 있으신 분\", \"ios\"]")
        List<String> screenerQuestions
) {

    public static PostScreenerQuestionResponse of(List<String> questions) {
        return new PostScreenerQuestionResponse(questions);
    }
}
