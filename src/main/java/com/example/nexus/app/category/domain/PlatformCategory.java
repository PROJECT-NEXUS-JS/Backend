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
    @Schema(description = "Android")
    ANDROID_GAME("Android", MainCategory.GAME),

    @Schema(description = "iOS")
    IOS_GAME("iOS", MainCategory.GAME),

    @Schema(description = "PC 플라이언트")
    PC("PC 플라이언트", MainCategory.GAME),

    @Schema(description = "Steam VR")
    STEAM_VR("Steam VR", MainCategory.GAME),

    @Schema(description = "Play Station")
    PLAY_STATION("Play Station", MainCategory.GAME),

    @Schema(description = "Xbox")
    XBOX("Xbox", MainCategory.GAME),

    @Schema(description = "Meta Quest")
    META_QUEST("Meta Quest", MainCategory.GAME),

    @Schema(description = "기타 (게임)")
    GAME_ETC("기타", MainCategory.GAME),

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
