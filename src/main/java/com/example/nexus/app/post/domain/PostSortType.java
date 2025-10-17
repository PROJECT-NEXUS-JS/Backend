package com.example.nexus.app.post.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "게시글 정렬 타입")
public enum PostSortType {
    @Schema(description = "최신순")
    LATEST("latest", "최신순"),

    @Schema(description = "인기순")
    POPULAR("popular", "인기순"),

    @Schema(description = "마감임박순")
    DEADLINE("deadline", "마감임박순"),

    @Schema(description = "조회수순")
    VIEW_COUNT("viewCount", "조회수순");

    private final String code;
    private final String description;

    public static PostSortType fromCode(String code) {
        if (code == null) {
            return LATEST;
        }

        for (PostSortType sortType : values()) {
            if (sortType.code.equalsIgnoreCase(code)) {
                return sortType;
            }
        }

        return LATEST;
    }
}
