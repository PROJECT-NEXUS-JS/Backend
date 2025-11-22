package com.example.nexus.app.user.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.user.dto.UserInterestDto;
import com.example.nexus.app.user.service.UserInterestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 관심사", description = "사용자 관심사 관리 API")
@RestController
@RequestMapping("/v1/users/interests")
@RequiredArgsConstructor
public class UserInterestController {

    private final UserInterestService userInterestService;

    @Operation(summary = "사용자 관심사 조회", description = "현재 로그인한 사용자의 관심사를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<UserInterestDto.UserInterestResponse>> getUserInterest(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        UserInterestDto.UserInterestResponse response = userInterestService.getUserInterest(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "사용자 관심사 설정", description = "사용자의 관심사를 설정합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<UserInterestDto.UserInterestResponse>> setUserInterest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserInterestDto.UserInterestRequest request) {
        
        UserInterestDto.UserInterestResponse response = userInterestService.setUserInterest(
                userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}