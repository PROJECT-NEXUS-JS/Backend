package com.example.nexus.app.post.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostStatsResponse(
        @Schema(description = "전체 게시글 수")
        long totalPosts,

        @Schema(description = "활성 게시글 수")
        long activePosts,

        @Schema(description = "전체 참여자 수")
        long totalParticipants,

        @Schema(description = "전체 좋아요 수")
        long totalLikes
) {

    public static PostStatsResponse of(long totalPosts, long activePosts, long totalParticipants, long totalLikes) {
        return new PostStatsResponse(totalPosts, activePosts, totalParticipants, totalLikes);
    }
}
