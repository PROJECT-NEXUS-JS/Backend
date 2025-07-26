package com.example.nexus.app.post.controller.dto.request;

import com.example.nexus.app.post.domain.RewardType;
import io.swagger.v3.oas.annotations.media.Schema;

public record PostSearchRequest(
        @Schema(description = "검색 키워드")
        String keyword,

        @Schema(description = "메인 카테고리 ID")
        Long mainCategoryId,

        @Schema(description = "서브 카테고리 ID")
        Long subCategoryId,

        @Schema(description = "리워드 타입")
        RewardType rewardType,

        @Schema(description = "정렬 기준 (latest, popular, deadline)")
        String sortBy
) {

    public static PostSearchRequest of(String keyword, Long mainCategoryId, Long subCategoryId,
                                       RewardType rewardType, String sortBy) {
        return new PostSearchRequest(keyword, mainCategoryId, subCategoryId, rewardType, sortBy);
    }
}
