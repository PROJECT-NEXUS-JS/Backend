package com.example.nexus.app.post.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostSortType {
    LATEST("latest", "최신순"),
    POPULAR("popular", "인기순"),
    DEADLINE("deadline", "마감임박순"),
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
