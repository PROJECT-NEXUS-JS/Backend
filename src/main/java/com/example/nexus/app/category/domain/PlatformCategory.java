package com.example.nexus.app.category.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Schema(description = "플랫폼 카테고리")
public enum PlatformCategory {
    // 웹 플랫폼
    @Schema(description = "전체 (웹)")
    WEB_ALL("전체", MainCategory.WEB),

    // 앱 플랫폼
    @Schema(description = "안드로이드")
    ANDROID("안드로이드", MainCategory.APP),

    @Schema(description = "iOS")
    IOS("iOS", MainCategory.APP),

    @Schema(description = "전체 (앱)")
    APP_ALL("전체", MainCategory.APP),

    // 게임 플랫폼
    @Schema(description = "PC")
    PC("PC", MainCategory.GAME),

    @Schema(description = "모바일")
    MOBILE("모바일", MainCategory.GAME),

    @Schema(description = "콘솔")
    CONSOLE("콘솔", MainCategory.GAME),

    @Schema(description = "VR")
    VR("VR", MainCategory.GAME),

    @Schema(description = "전체 (게임)")
    GAME_ALL("전체", MainCategory.GAME),

    // 기타 플랫폼
    @Schema(description = "전체 (기타)")
    ETC_ALL("전체", MainCategory.ETC);

    private final String description;
    private final MainCategory mainCategory;

    public static List<PlatformCategory> getByMainCategory(MainCategory mainCategory) {
        return Arrays.stream(values())
                .filter(platform -> platform.mainCategory == mainCategory)
                .toList();
    }
}
