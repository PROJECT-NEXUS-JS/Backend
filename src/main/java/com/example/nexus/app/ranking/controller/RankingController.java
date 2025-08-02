package com.example.nexus.app.ranking.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.ranking.dto.FullRankingResponse;
import com.example.nexus.app.ranking.dto.HomeRankingResponse;
import com.example.nexus.app.ranking.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "랭킹", description = "랭킹 API")
@RestController
@RequestMapping("/v1/users/posts")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @Operation(summary = "홈 화면 랭킹 조회", description = "오늘의 추천(개인화), 마감 임박, 인기있는 테스트, 방금 등록한 테스트 섹션을 조회합니다. (각 섹션당 4개씩)")
    @GetMapping("/home-ranking")
    public ApiResponse<HomeRankingResponse> getHomeRanking(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Long userId = userDetails != null ? userDetails.getUserId() : null;
        HomeRankingResponse response = rankingService.getHomeRanking(userId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "전체보기 랭킹 조회", description = "페이징과 필터링이 적용된 전체 랭킹을 조회합니다.")
    @GetMapping("/ranking")
    public ApiResponse<FullRankingResponse> getFullRanking(
            @Parameter(description = "랭킹 타입 (popular, recent, deadline_imminent, participation_count)")
            @RequestParam(defaultValue = "popular") String rankingType,
            
            @Parameter(description = "메인 카테고리 (WEB, APP, GAME, ETC)")
            @RequestParam(required = false) String mainCategory,
            
            @Parameter(description = "플랫폼 카테고리")
            @RequestParam(required = false) String platformCategory,
            
            @PageableDefault(size = 20) Pageable pageable) {
        
        FullRankingResponse response = rankingService.getFullRanking(rankingType, mainCategory, platformCategory, pageable);
        return ApiResponse.onSuccess(response);
    }
}
