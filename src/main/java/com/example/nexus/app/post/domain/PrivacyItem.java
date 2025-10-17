package com.example.nexus.app.post.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "개인정보 수집 항목")
public enum PrivacyItem {
    @Schema(description = "이름")
    NAME("이름"),

    @Schema(description = "이메일")
    EMAIL("이메일"),

    @Schema(description = "연락처")
    CONTACT("연락처"),

    @Schema(description = "기타")
    OTHER("기타");

    private final String description;
}
