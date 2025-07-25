package com.example.nexus.app.post.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    COMPLETED("완료"),
    CANCELLED("취소");

    private final String description;
}
