package com.example.nexus.app.post.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RewardType {
    CASH("현금 지급"),
    GIFT_CARD("기프티콘"),
    PRODUCT("제품 지급"),
    NONE("보상 없음");

    private final String description;
}
