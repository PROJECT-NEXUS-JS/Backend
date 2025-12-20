package com.example.nexus.app.reward.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "리워드 타입")
public enum RewardType {
    @Schema(description = "현금 지급")
    CASH("현금 지급"),

    @Schema(description = "기프티콘")
    GIFT_CARD("기프티콘"),

    @Schema(description = "제품 지급")
    PRODUCT("제품 지급"),

    @Schema(description = "기타")
    ETC("기타"),

    @Schema(description = "보상 없음")
    NONE("보상 없음");

    private final String description;
}
