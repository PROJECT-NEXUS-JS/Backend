package com.example.nexus.app.ranking.service;

import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import com.example.nexus.app.post.repository.PostRepository;
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

    private final PostRepository postRepository;

    /**
     * 홈 화면 랭킹 조회
     * 오늘의 추천, 마감 임박, 인기있는 테스트 섹션 제공
     */
    public HomeRankingResponse getHomeRanking() {
        // 오늘의 추천 (인기순 + 최신순 혼합)
        List<Post> todayRecommendations = postRepository.findTodayRecommendations(
                PostStatus.ACTIVE, 
                PageRequest.of(0, 10)
        );
        
        // 마감 임박 (7일 이내 마감)
        LocalDateTime sevenDaysFromNow = LocalDateTime.now().plusDays(7);
        List<Post> deadlineImminent = postRepository.findDeadlineImminentForHome(
                PostStatus.ACTIVE, 
                sevenDaysFromNow,
                PageRequest.of(0, 10)
        );
        
        // 인기있는 테스트 (인기순)
        List<Post> popularTests = postRepository.findPopularForHome(
                PostStatus.ACTIVE, 
                PageRequest.of(0, 10)
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
                case "popular" -> postRepository.findPopularPostsByCategory(PostStatus.ACTIVE, mainCategory, platformCategory, pageable);
                case "recent" -> postRepository.findRecentPostsByCategory(PostStatus.ACTIVE, mainCategory, platformCategory, pageable);
                case "deadline_imminent" -> postRepository.findDeadlineImminentPostsByCategory(PostStatus.ACTIVE, mainCategory, platformCategory, pageable);
                case "participation_count" -> postRepository.findParticipationCountPostsByCategory(PostStatus.ACTIVE, mainCategory, platformCategory, pageable);
                default -> postRepository.findPopularPostsByCategory(PostStatus.ACTIVE, mainCategory, platformCategory, pageable);
            };
        }
        
        // 카테고리 필터링이 없는 경우
        return switch (rankingType) {
            case "popular" -> postRepository.findPopularPosts(PostStatus.ACTIVE, pageable);
            case "recent" -> postRepository.findRecentPosts(PostStatus.ACTIVE, pageable);
            case "deadline_imminent" -> postRepository.findDeadlineImminentPosts(PostStatus.ACTIVE, pageable);
            case "participation_count" -> postRepository.findParticipationCountPosts(PostStatus.ACTIVE, pageable);
            default -> postRepository.findPopularPosts(PostStatus.ACTIVE, pageable);
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