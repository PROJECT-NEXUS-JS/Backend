package com.example.nexus.app.category.dto.response;

import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.category.domain.GenreCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponse(
    @Schema(description = "카테고리 ID")
    String id,
    
    @Schema(description = "카테고리 이름")
    String name
) {
    
    public static CategoryResponse from(MainCategory category) {
        return new CategoryResponse(
                category.name(),
                category.getDisplayName()
        );
    }
    
    public static CategoryResponse from(PlatformCategory category) {
        return new CategoryResponse(
                category.name(),
                category.getDisplayName()
        );
    }
    
    public static CategoryResponse from(GenreCategory category) {
        return new CategoryResponse(
                category.getId().toString(),
                category.getName()
        );
    }
}