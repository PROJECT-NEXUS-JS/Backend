package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.category.dto.response.CategoryResponse;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.reward.domain.RewardType;
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
        List<CategoryResponse> genreCategories,

        @Schema(description = "리워드 제공 여부")
        Boolean hasReward
) {
    public static PostSummaryResponse from(Post post) {
        boolean hasReward = post.getReward() != null
                && post.getReward().getRewardType() != null
                && post.getReward().getRewardType() != RewardType.NONE;

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
                        .toList(),
                hasReward
        );
    }
}
