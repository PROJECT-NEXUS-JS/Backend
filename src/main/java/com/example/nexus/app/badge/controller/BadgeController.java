package com.example.nexus.app.badge.controller;

import com.example.nexus.app.badge.dto.UserBadgeResponse;
import com.example.nexus.app.badge.dto.UserBadgeSummaryResponse;
import com.example.nexus.app.badge.service.BadgeService;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        List<UserBadgeResponse> badges = badgeService.getUserBadges(userDetails.getUserId())
                .stream()
                .map(UserBadgeResponse::from)
                .toList();

        Long totalCount = (long) badges.size();
        UserBadgeSummaryResponse response = UserBadgeSummaryResponse.of(
                userDetails.getUserId(),
                totalCount,
                badges
        );

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "특정 사용자 뱃지 목록 조회", description = "특정 사용자가 획득한 뱃지 목록을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserBadgeSummaryResponse>> getUserBadges(
            @PathVariable Long userId
    ) {
        List<UserBadgeResponse> badges = badgeService.getUserBadges(userId)
                .stream()
                .map(UserBadgeResponse::from)
                .toList();

        Long totalCount = (long) badges.size();
        UserBadgeSummaryResponse response = UserBadgeSummaryResponse.of(
                userId,
                totalCount,
                badges
        );

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}

