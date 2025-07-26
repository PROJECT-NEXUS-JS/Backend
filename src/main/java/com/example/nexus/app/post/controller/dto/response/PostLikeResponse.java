package com.example.nexus.app.post.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostLikeResponse(
        @Schema(description = "좋아요 상태 (true: 좋아요, false: 좋아요 취소)")
        boolean liked,

        @Schema(description = "현재 좋아요 수")
        int likeCount
) {

    public static PostLikeResponse of(boolean liked, int likeCount) {
        return new PostLikeResponse(liked, likeCount);
    }
}
