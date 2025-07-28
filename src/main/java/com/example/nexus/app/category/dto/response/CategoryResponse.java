package com.example.nexus.app.category.dto.response;

import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.category.domain.GenreCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponse(
    @Schema(description = "카테고리 코드")
    String code,
    
    @Schema(description = "카테고리 이름")
    String name
) {
    
    public static CategoryResponse from(MainCategory category) {
        return new CategoryResponse(
                category.name(), // Enum name : "WEB", "APP" 등
                category.getDisplayName() // Display name: "웹", "앱" 등
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
                category.getCode(),
                category.getDisplayName()
        );
    }
}
