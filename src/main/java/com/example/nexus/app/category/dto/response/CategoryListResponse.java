package com.example.nexus.app.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

public record CategoryListResponse(
    @Schema(description = "메인 카테고리 목록")
    List<CategoryResponse> mainCategories,
    
    @Schema(description = "플랫폼 카테고리 목록 (메인 카테고리별)")
    Map<String, List<CategoryResponse>> platformCategories,
    
    @Schema(description = "장르 카테고리 목록 (메인 카테고리와 독립적)")
    List<CategoryResponse> genreCategories
) {
    
    public static CategoryListResponse of(List<CategoryResponse> mainCategories,
                                        Map<String, List<CategoryResponse>> platformCategories,
                                        List<CategoryResponse> genreCategories) {
        return new CategoryListResponse(mainCategories, platformCategories, genreCategories);
    }
}