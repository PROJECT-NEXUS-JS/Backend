package com.example.nexus.app.message.controller.dto.response;

import com.example.nexus.app.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;

public record RoomPostInfo(
        @Schema(description = "게시글 ID")
        Long postId,

        @Schema(description = "게시글 제목")
        String title
) {
    public static RoomPostInfo from(Post post) {
        return new RoomPostInfo(
                post.getId(),
                post.getTitle()
        );
    }
}
