package com.example.nexus.app.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record BasicInfoUpdateRequest(
    @NotBlank(message = "활동명은 필수입니다.")
    @Size(min = 1, max = 20, message = "활동명은 1자 이상 20자 이하여야 합니다.")
    String nickname
) {
}
