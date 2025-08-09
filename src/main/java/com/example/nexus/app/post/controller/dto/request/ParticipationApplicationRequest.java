package com.example.nexus.app.post.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record ParticipationApplicationRequest(
        @Schema(description = "신청자 이름", example = "홍길동")
        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 10, message = "이름은 최대 10글자까지 입력 가능합니다.")
        String applicantName,

        @Schema(description = "연락처 (숫자만)", example = "01012345678")
        @NotBlank(message = "연락처는 필수입니다.")
        @Pattern(regexp = "^[0-9]+$", message = "연락처는 숫자만 입력 가능합니다.")
        @Size(min = 10, max = 11, message = "연락처는 10-11자리 숫자를 입력해주세요.")
        String contactNumber,

        @Schema(description = "이메일", example = "hong@example.com")
        @Email(message = "이메일 형식이 맞지 않습니다.")
        String applicantEmail,

        @Schema(description = "신청 사유", example = "UI/UX 개선에 관심이 많아 참여하고 싶습니다.")
        @NotBlank(message = "신청 사유는 필수입니다.")
        @Size(max = 1000, message = "신청 사유는 최대 1000자까지 입력 가능합니다.")
        String applicationReason,

        @Schema(description = "개인정보 수집/이용 동의", example = "true")
        @NotNull(message = "개인정보 동의는 필수입니다.")
        @AssertTrue(message = "개인정보 수집/이용에 동의해야 합니다.")
        Boolean privacyAgreement,

        @Schema(description = "참여 조건 동의", example = "true")
        @NotNull(message = "참여 조건 동의는 필수입니다.")
        @AssertTrue(message = "참여 조건에 동의해야 합니다.")
        Boolean termsAgreement
) {}
