package com.example.nexus.app.recruitment.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RecruitmentProfileUpdateRequest(
        @Schema(description = "닉네임 (2-10자, 특수문자 불가)", example = "우홍홍")
        @Size(min = 2, max = 10, message = "닉네임은 2-10자여야 합니다")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다")
        String nickname,

        @Schema(description = "호스트 소개글 (최대 300자)", example = "안녕하세요! 함께 프로젝트를 만들어갈 팀원을 찾고 있습니다.")
        @Size(max = 300, message = "호스트 소개는 최대 300자까지 입력 가능합니다")
        String introduction
) {}
