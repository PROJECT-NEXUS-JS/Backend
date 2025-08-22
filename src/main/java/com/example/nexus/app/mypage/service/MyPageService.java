package com.example.nexus.app.mypage.service;

import com.example.nexus.app.mypage.dto.DashboardDto;
import com.example.nexus.app.mypage.dto.RecentlyViewedTestDto;
import com.example.nexus.app.mypage.dto.TotalParticipationDto;
import com.example.nexus.app.mypage.dto.WatchlistDto;
import com.example.nexus.app.mypage.dto.TestDeadlineDto;
import com.example.nexus.app.mypage.domain.RecentViewedPost;
import com.example.nexus.app.mypage.repository.RecentViewedPostRepository;
import com.example.nexus.app.post.domain.ParticipationStatus;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.repository.ParticipationRepository;
import com.example.nexus.app.post.repository.PostLikeRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final RecentViewedPostRepository recentViewedPostRepository;
    private final PostLikeRepository postLikeRepository;
    private final ParticipationRepository participationRepository;

    public DashboardDto getDashboardData(Long userId) {
        if (userId == null) {
            return DashboardDto.builder()
                    .recentlyViewedTests(Collections.emptyList())
                    .build();
        }

        List<RecentViewedPost> recentViewedPosts = recentViewedPostRepository.findByUserIdOrderByViewedAtDesc(userId);

        List<RecentlyViewedTestDto> recentTests = recentViewedPosts.stream()
                .map(recentView -> {
                    Post post = recentView.getPost();
                    return RecentlyViewedTestDto.builder()
                            .postId(post.getId())
                            .category(post.getMainCategory().stream().map(Enum::toString).collect(Collectors.joining(", ")))
                            .title(post.getTitle())
                            .oneLineIntro(post.getServiceSummary())
                            .tags(post.getGenreCategories().stream().map(Enum::toString).collect(Collectors.toList()))
                            .viewedAt(recentView.getViewedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return DashboardDto.builder()
                .recentlyViewedTests(recentTests)
                .build();
    }

    public WatchlistDto getWatchlistData(Long userId) {
        if (userId == null) {
            return WatchlistDto.builder()
                    .testsNearingDeadline(Collections.emptyList())
                    .build();
        }

        List<Post> nearingDeadlinePosts = postLikeRepository.findLikedPostsWithNearingDeadline(userId);

        List<TestDeadlineDto> nearingDeadlineTests = nearingDeadlinePosts.stream()
                .map(post -> TestDeadlineDto.builder()
                        .postId(post.getId())
                        .category(post.getMainCategory().stream().map(Enum::toString).collect(Collectors.joining(", ")))
                        .title(post.getTitle())
                        .tags(post.getGenreCategories().stream().map(Enum::toString).collect(Collectors.toList()))
                        .deadline(post.getSchedule().getRecruitmentDeadline().toLocalDate())
                        .build())
                .collect(Collectors.toList());

        return WatchlistDto.builder()
                .testsNearingDeadline(nearingDeadlineTests)
                .build();
    }

    public TotalParticipationDto getTotalParticipationData(Long userId) {
        if (userId == null) {
            return TotalParticipationDto.builder()
                    .totalCount(0)
                    .countByCategory(Collections.emptyMap())
                    .build();
        }

        List<Post> participatedPosts = participationRepository.findPostsByUserIdAndStatus(userId, ParticipationStatus.APPROVED);

        int totalCount = participatedPosts.size();

        Map<String, Integer> countByCategory = participatedPosts.stream()
                .flatMap(post -> post.getMainCategory().stream())
                .collect(Collectors.groupingBy(Enum::toString, Collectors.summingInt(e -> 1)));

        return TotalParticipationDto.builder()
                .totalCount(totalCount)
                .countByCategory(countByCategory)
                .build();
    }
}
