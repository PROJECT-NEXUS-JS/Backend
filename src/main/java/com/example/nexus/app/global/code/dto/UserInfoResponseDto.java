package com.example.nexus.app.global.code.dto;

import com.example.nexus.app.user.domain.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private String email;
    private String nickname;
    private String profileUrl;
    private String lastLoginAt;
    private RoleType roleType;
    private String job;
    private List<String> interests;
}
