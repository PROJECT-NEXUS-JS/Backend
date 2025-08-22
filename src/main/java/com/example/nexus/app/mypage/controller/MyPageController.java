package com.example.nexus.app.mypage.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.mypage.dto.DashboardDto;
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
        Long userId = userDetails.getUserId();
        DashboardDto dashboardData = myPageService.getDashboardData(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(dashboardData));
    }
}
