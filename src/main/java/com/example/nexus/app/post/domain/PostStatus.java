package com.example.nexus.app.post.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "게시글 상태")
public enum PostStatus {
    @Schema(description = "임시저장")
    DRAFT("임시저장"),

    @Schema(description = "활성")
    ACTIVE("활성"),

    @Schema(description = "비활성")
    INACTIVE("비활성"),

    @Schema(description = "완료")
    COMPLETED("완료"),

    @Schema(description = "취소")
    CANCELLED("취소"),

    @Schema(description = "만료")
    EXPIRED("만료");

    private final String description;
}
