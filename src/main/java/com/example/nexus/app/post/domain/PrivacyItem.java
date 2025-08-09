package com.example.nexus.app.post.domain;

import lombok.Getter;

@Getter
public enum PrivacyItem {
    NAME("이름"),
    EMAIL("이메일"),
    CONTACT("연락처"),
    OTHER("기타");

    private final String description;

    PrivacyItem(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
