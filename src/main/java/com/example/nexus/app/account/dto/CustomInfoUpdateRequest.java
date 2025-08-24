package com.example.nexus.app.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record CustomInfoUpdateRequest(
    @NotBlank(message = "직업은 필수입니다.")
    String job,
    
    @Pattern(regexp = "^[0-9]{4}년$", message = "출생년도는 'YYYY년' 형식이어야 합니다.")
    String birthYear,
    
    @NotBlank(message = "성별은 필수입니다.")
    @Size(max = 10, message = "성별은 10자 이하여야 합니다.")
    String gender,
    
    List<String> interests,
    
    List<String> preferredGenres
) {
}
