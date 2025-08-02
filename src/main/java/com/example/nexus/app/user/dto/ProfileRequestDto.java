package com.example.nexus.app.user.dto;

import jakarta.validation.constraints.NotBlank; // [추가]
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProfileRequestDto {

    @NotBlank(message = "직업을 입력해주세요.")
    @Size(max = 15, message = "직업은 15자 이내로 입력해주세요.")
    private String job;

    @NotEmpty(message = "관심사를 하나 이상 선택해주세요.")
    @Size(max = 5, message = "관심사는 최대 5개까지 선택할 수 있습니다.")
    private List<String> interests;
}