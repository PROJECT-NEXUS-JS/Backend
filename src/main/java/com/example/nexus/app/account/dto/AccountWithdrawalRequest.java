package com.example.nexus.app.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AccountWithdrawalRequest(
    @NotBlank(message = "탈퇴 확인 문구를 입력해주세요.")
    String confirmation
) {
}
