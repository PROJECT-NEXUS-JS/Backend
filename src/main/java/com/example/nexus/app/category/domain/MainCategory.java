package com.example.nexus.app.category.domain;

import lombok.Getter;

@Getter
public enum MainCategory {
    WEB("웹"),
    APP("앱"),
    GAME("게임"),
    ETC("기타");
    
    private final String displayName;
    
    MainCategory(String displayName) {
        this.displayName = displayName;
    }
}