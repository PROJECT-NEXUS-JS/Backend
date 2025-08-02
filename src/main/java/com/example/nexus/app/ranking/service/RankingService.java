package com.example.nexus.app.ranking.service;

import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import com.example.nexus.app.ranking.repository.RankingRepository;
import com.example.nexus.app.ranking.dto.FullRankingResponse;
import com.example.nexus.app.ranking.dto.HomeRankingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final RankingRepository rankingRepository;

    /**
     * 홈 화면 랭킹 조회 (4개씩)
     * 오늘의 추천, 마감 임박, 인기있는 테스트 조회
     */
    public HomeRankingResponse getHomeRanking() {
        // 오늘의 추천 (인기순 + 최신순)
        List<Post> todayRecommendations = rankingRepository.findTodayRecommendations(
                PostStatus.ACTIVE, 
                PageRequest.of(0, 4)
        );
        
        // 마감 임박 (7일)
        LocalDateTime sevenDaysFromNow = LocalDateTime.now().plusDays(7);
        List<Post> deadlineImminent = rankingRepository.findDeadlineImminentForHome(
                PostStatus.ACTIVE, 
                sevenDaysFromNow,
                PageRequest.of(0, 4)
        );
        
        // 인기있는 테스트 - 4개
        List<Post> popularTests = rankingRepository.findPopularForHome(
                PostStatus.ACTIVE, 
                PageRequest.of(0, 4)
        );

        return HomeRankingResponse.builder()
                .todayRecommendations(todayRecommendations.stream()
                        .map(HomeRankingResponse::from)
                        .toList())
                .deadlineImminent(deadlineImminent.stream()
                        .map(HomeRankingResponse::from)
                        .toList())
                .popularTests(popularTests.stream()
                        .map(HomeRankingResponse::from)
                        .toList())
                .build();
    }

    /**
     * 전체보기 랭킹 조회
     */
    public FullRankingResponse getFullRanking(String rankingType, String mainCategory, 
                                            String platformCategory, Pageable pageable) {
        MainCategory mainCat = parseMainCategory(mainCategory);
        PlatformCategory platformCat = parsePlatformCategory(platformCategory);

        Page<Post> posts = getRankedPosts(rankingType, mainCat, platformCat, pageable);
        
        return FullRankingResponse.from(posts);
    }

    /**
     * 랭킹 타입에 따른 게시글 조회
     */
    private Page<Post> getRankedPosts(String rankingType, MainCategory mainCategory, 
                                     PlatformCategory platformCategory, Pageable pageable) {
        
        // 카테고리 필터링이 있는 경우
        if (mainCategory != null || platformCategory != null) {
            return switch (rankingType) {
                case "popular" -> rankingRepository.findPopularPostsByCategory(PostStatus.ACTIVE, mainCategory, platformCategory, pageable);
                case "recent" -> rankingRepository.findRecentPostsByCategory(PostStatus.ACTIVE, mainCategory, platformCategory, pageable);
                case "deadline_imminent" -> rankingRepository.findDeadlineImminentPostsByCategory(PostStatus.ACTIVE, mainCategory, platformCategory, pageable);
                case "participation_count" -> rankingRepository.findParticipationCountPostsByCategory(PostStatus.ACTIVE, mainCategory, platformCategory, pageable);
                default -> rankingRepository.findPopularPostsByCategory(PostStatus.ACTIVE, mainCategory, platformCategory, pageable);
            };
        }
        
        // 카테고리 필터링이 없는 경우
        return switch (rankingType) {
            case "popular" -> rankingRepository.findPopularPosts(PostStatus.ACTIVE, pageable);
            case "recent" -> rankingRepository.findRecentPosts(PostStatus.ACTIVE, pageable);
            case "deadline_imminent" -> rankingRepository.findDeadlineImminentPosts(PostStatus.ACTIVE, pageable);
            case "participation_count" -> rankingRepository.findParticipationCountPosts(PostStatus.ACTIVE, pageable);
            default -> rankingRepository.findPopularPosts(PostStatus.ACTIVE, pageable);
        };
    }

    /**
     * 메인 카테고리 파싱
     */
    private MainCategory parseMainCategory(String mainCategory) {
        if (mainCategory == null || mainCategory.isBlank()) {
            return null;
        }
        try {
            return MainCategory.valueOf(mainCategory.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid main category: {}", mainCategory);
            return null;
        }
    }

    /**
     * 플랫폼 카테고리 파싱
     */
    private PlatformCategory parsePlatformCategory(String platformCategory) {
        if (platformCategory == null || platformCategory.isBlank()) {
            return null;
        }
        try {
            return PlatformCategory.valueOf(platformCategory.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid platform category: {}", platformCategory);
            return null;
        }
    }
} 
