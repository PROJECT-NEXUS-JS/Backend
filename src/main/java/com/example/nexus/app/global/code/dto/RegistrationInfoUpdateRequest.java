package com.example.nexus.app.global.code.dto;

import com.example.nexus.app.user.domain.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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

    @NotNull(message = "성별은 필수 입력 항목입니다. 0:m, 1:w")
    private Gender gender;

    @NotNull(message = "생년월일은 필수 입력 항목입니다. yyyy-mm-dd")
    @Past(message = "생년월일은 미래 날짜일 수 없습니다.")
    private LocalDate birthDate;
}
