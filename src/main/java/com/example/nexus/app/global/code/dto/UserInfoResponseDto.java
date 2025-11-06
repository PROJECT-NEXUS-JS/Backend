package com.example.nexus.app.global.code.dto;

import com.example.nexus.app.user.domain.Gender;
import com.example.nexus.app.user.domain.RoleType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
    private Gender gender;
    private LocalDate birthDate;
}
