package com.example.nexus.app.message.domain;

public enum MessageType {
    TEXT("텍스트"),
    FILE("파일"),
    IMAGE("이미지");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
