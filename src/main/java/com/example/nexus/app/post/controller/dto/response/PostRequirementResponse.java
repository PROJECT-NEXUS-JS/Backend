package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.post.domain.PostRequirement;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PostRequirementResponse(
        @Schema(description = "최대 참여자 수")
        Integer maxParticipants,

        @Schema(description = "성별 요구사항")
        String genderRequirement,

        @Schema(description = "최소 나이")
        Integer ageMin,

        @Schema(description = "최대 나이")
        Integer ageMax,

        @Schema(description = "추가 요구사항")
        List<String> screenerQuestions
) {

    public static PostRequirementResponse from(PostRequirement requirement) {
        return new PostRequirementResponse(
                requirement.getMaxParticipants(),
                requirement.getGenderRequirement(),
                requirement.getAgeMin(),
                requirement.getAgeMax(),
                requirement.getScreenerQuestions()
        );
    }
}
