package com.example.nexus.app.post.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostStatus {
    DRAFT("임시저장"),
    ACTIVE("활성"),
    INACTIVE("비활성"),
    COMPLETED("완료"),
    CANCELLED("취소"),
    EXPIRED("만료");

    private final String description;
}
