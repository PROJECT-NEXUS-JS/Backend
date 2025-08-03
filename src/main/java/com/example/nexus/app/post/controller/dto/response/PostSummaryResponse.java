package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.category.dto.response.CategoryResponse;
import com.example.nexus.app.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostSummaryResponse(
        @Schema(description = "게시글 ID")
        Long id,

        @Schema(description = "제목")
        String title,

        @Schema(description = "서비스 요약")
        String serviceSummary,

        @Schema(description = "썸네일 URL")
        String thumbnailUrl,

        @Schema(description = "메인 카테고리 목록")
        List<CategoryResponse> mainCategories,

        @Schema(description = "플랫폼 카테고리 목록")
        List<CategoryResponse> platformCategories,

        @Schema(description = "장르 카테고리 목록")
        List<CategoryResponse> genreCategories
) {
    public static PostSummaryResponse from(Post post) {
        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getServiceSummary(),
                post.getThumbnailUrl(),
                post.getMainCategory().stream()
                        .map(CategoryResponse::from)
                        .toList(),
                post.getPlatformCategory().stream()
                        .map(CategoryResponse::from)
                        .toList(),
                post.getGenreCategories().stream()
                        .map(CategoryResponse::from)
                        .toList()
        );
    }
}
