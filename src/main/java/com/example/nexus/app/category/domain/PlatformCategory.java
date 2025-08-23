package com.example.nexus.app.category.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum PlatformCategory {
    // 웹 플랫폼
    WEB_ALL("전체", MainCategory.WEB),
    
    // 앱 플랫폼
    ANDROID("안드로이드", MainCategory.APP),
    IOS("iOS", MainCategory.APP),
    APP_ALL("전체", MainCategory.APP),
    
    // 게임 플랫폼
    PC("PC", MainCategory.GAME),
    MOBILE("모바일", MainCategory.GAME),
    CONSOLE("콘솔", MainCategory.GAME),
    VR("VR", MainCategory.GAME),
    GAME_ALL("전체", MainCategory.GAME),
    
    // 기타 플랫폼
    ETC_ALL("전체", MainCategory.ETC);
    
    private final String displayName;
    private final MainCategory mainCategory;
    
    PlatformCategory(String displayName, MainCategory mainCategory) {
        this.displayName = displayName;
        this.mainCategory = mainCategory;
    }
    
    public static List<PlatformCategory> getByMainCategory(MainCategory mainCategory) {
        return Arrays.stream(values())
                .filter(platform -> platform.mainCategory == mainCategory)
                .toList();
    }
}
