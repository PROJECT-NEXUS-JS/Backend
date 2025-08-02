package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.post.domain.PostReward;
import com.example.nexus.app.post.domain.RewardType;
import io.swagger.v3.oas.annotations.media.Schema;

public record PostRewardResponse(
        @Schema(description = "리워드 타입")
        RewardType rewardType,

        @Schema(description = "리워드 상세 설명")
        String rewardDescription
) {

    public static PostRewardResponse from(PostReward reward) {
        return new PostRewardResponse(
                reward.getRewardType(),
                reward.getRewardDescription()
        );
    }
}
