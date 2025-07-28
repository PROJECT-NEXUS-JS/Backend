package com.example.nexus.app.post.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostLikeToggleResponse(
        @Schema(description = "찜하기 토글 상태")
        boolean liked,

        @Schema(description = "총 찜 개수")
        Long likeCount
) {

    public static PostLikeToggleResponse of(boolean liked, Long likeCount) {
        return new PostLikeToggleResponse(liked, likeCount);
    }
}
