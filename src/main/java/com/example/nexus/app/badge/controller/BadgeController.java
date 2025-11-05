package com.example.nexus.app.badge.controller;

import com.example.nexus.app.badge.dto.UserBadgeSummaryResponse;
import com.example.nexus.app.badge.service.BadgeService;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 뱃지 컨트롤러
 */
@Tag(name = "뱃지", description = "뱃지 관련 API")
@RestController
@RequestMapping("/v1/users/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @Operation(summary = "내 뱃지 목록 조회", description = "로그인한 사용자가 획득한 뱃지 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<UserBadgeSummaryResponse>> getMyBadges(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserBadgeSummaryResponse response = badgeService.getUserBadgeSummary(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "특정 사용자 뱃지 목록 조회", description = "특정 사용자가 획득한 뱃지 목록을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserBadgeSummaryResponse>> getUserBadges(
            @PathVariable Long userId
    ) {
        UserBadgeSummaryResponse response = badgeService.getUserBadgeSummary(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}

