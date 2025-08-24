package com.example.nexus.app.global.code.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class UserInfoUpdateRequest {

    @Schema(description = "닉네임 (2-10자, 특수문자 불가)", example = "우홍홍")
    @Size(min = 2, max = 10, message = "닉네임은 2-10자여야 합니다")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다")
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @Schema(description = "직업", example = "개발자")
    @NotBlank(message = "직업을 입력해주세요.")
    @Size(max = 15, message = "직업은 15자 이내로 입력해주세요.")
    private String job;

    @Schema(description = "관심사 목록", example = "[\"웹개발\", \"모바일앱\", \"AI\"]")
    @NotEmpty(message = "관심사를 하나 이상 선택해주세요.")
    @Size(max = 5, message = "관심사는 최대 5개까지 선택할 수 있습니다.")
    private List<String> interests;
}
