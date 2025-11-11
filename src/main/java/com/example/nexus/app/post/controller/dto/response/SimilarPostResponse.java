package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.reward.domain.RewardType;
import lombok.Builder;

import java.util.stream.Collectors;

@Builder
public record SimilarPostResponse(
        Long id,
        String thumbnailUrl,
        String categories,
        String title,
        String oneLineIntro,
        boolean rewardProvided,
        String durationType
) {
    public static SimilarPostResponse from(Post post) {
        String mainCategoryStr = post.getMainCategory().stream()
                .map(MainCategory::name) // getDescription() 대신 name() 사용
                .collect(Collectors.joining(", "));
        String genreCategoryStr = post.getGenreCategories().stream()
                .map(GenreCategory::name) // getDescription() 대신 name() 사용
                .collect(Collectors.joining(", "));

        String combinedCategories = "";
        if (!mainCategoryStr.isEmpty()) {
            combinedCategories += mainCategoryStr;
        }
        if (!genreCategoryStr.isEmpty()) {
            if (!combinedCategories.isEmpty()) combinedCategories += " · ";
            combinedCategories += genreCategoryStr;
        }

        String durationType = "기간 미정";
        if (post.getSchedule() != null && post.getSchedule().getDurationTime() != null) {
            String duration = post.getSchedule().getDurationTime();
            if (duration.contains("일") || duration.contains("주") && !duration.contains("개월")) {
                durationType = "단기 테스트";
            } else if (duration.contains("개월")) {
                durationType = "중기 테스트";
            } else if (duration.contains("년")) {
                durationType = "장기 테스트";
            }
        }

        return SimilarPostResponse.builder()
                .id(post.getId())
                .thumbnailUrl(post.getThumbnailUrl())
                .categories(combinedCategories)
                .title(post.getTitle())
                .oneLineIntro(post.getServiceSummary())
                .rewardProvided(post.getReward() != null && post.getReward().getRewardType() != null && post.getReward().getRewardType() != RewardType.NONE)
                .durationType(durationType)
                .build();
    }
}
