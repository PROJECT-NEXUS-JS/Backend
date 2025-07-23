package com.example.nexus.app.global.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private String email;
    private String nickname;
    private String profileUrl;
    private String lastLoginAt;
}
