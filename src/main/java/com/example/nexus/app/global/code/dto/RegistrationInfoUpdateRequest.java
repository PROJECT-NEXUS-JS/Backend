package com.example.nexus.app.global.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationInfoUpdateRequest {
    @NotBlank(message = "직업은 필수 입력 항목입니다.")
    private String job;

    @Size(max = 5, message = "관심사는 최대 5개까지 선택할 수 있습니다.")
    private List<String> interests;
}
