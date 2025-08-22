package com.example.nexus.app.mypage.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.mypage.dto.DashboardDto;
import com.example.nexus.app.mypage.dto.ProfileDto;
import com.example.nexus.app.mypage.dto.TotalParticipationDto;
import com.example.nexus.app.mypage.dto.WatchlistDto;
import com.example.nexus.app.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "마이페이지", description = "마이페이지 관련 API")
@RestController
@RequestMapping("/v1/users/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(
            summary = "대시보드 조회",
            description = "로그인된 사용자의 대시보드 정보를 조회합니다. 최근 본 테스트 목록을 반환합니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardDto>> getDashboard(@Parameter(hidden = true)
                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        Long userId = userDetails.getUserId();
        DashboardDto dashboardData = myPageService.getDashboardData(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(dashboardData));
    }

    @Operation(
            summary = "관심 목록 조회",
            description = "로그인된 사용자의 관심 목록 중 마감 임박 테스트 목록을 조회합니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @GetMapping("/watchlist")
    public ResponseEntity<ApiResponse<WatchlistDto>> getWatchlist(@Parameter(hidden = true)
                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        Long userId = userDetails.getUserId();
        WatchlistDto watchlistData = myPageService.getWatchlistData(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(watchlistData));
    }

    @Operation(
            summary = "총 참여 프로젝트 갯수 조회",
            description = "로그인된 사용자의 총 참여 프로젝트 갯수와 분류별 갯수를 조회합니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @GetMapping("/total-participation")
    public ResponseEntity<ApiResponse<TotalParticipationDto>> getTotalParticipation(@Parameter(hidden = true)
                                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        Long userId = userDetails.getUserId();
        TotalParticipationDto totalParticipationData = myPageService.getTotalParticipationData(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(totalParticipationData));
    }

    @Operation(
            summary = "프로필 정보 조회",
            description = "로그인된 사용자의 프로필 정보(이름, 소속, 참여/게시글 수)를 조회합니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileDto>> getProfile(@Parameter(hidden = true)
                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        Long userId = userDetails.getUserId();
        ProfileDto profileData = myPageService.getProfileData(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(profileData));
    }
}
