package com.example.nexus.app.user.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.dto.LoginResponseDto; // [수정] LoginResponseDto를 import
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.user.dto.ProfileRequestDto;
import com.example.nexus.app.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Profile", description = "사용자 프로필 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "프로필(직업, 관심사) 등록 및 가입 완료", description = "최초 로그인(GUEST) 후 추가 정보를 입력하여 정회원(USER)으로 전환합니다. (GUEST 권한 필요)")
    @PutMapping("/profile")
    public ApiResponse<LoginResponseDto> completeSignUp(
                                                         @AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @Valid @RequestBody ProfileRequestDto requestDto) {

        LoginResponseDto responseDto = userProfileService.completeSignUp(userDetails.getUserId(), requestDto);
        return ApiResponse.onSuccess(responseDto);
    }
}
