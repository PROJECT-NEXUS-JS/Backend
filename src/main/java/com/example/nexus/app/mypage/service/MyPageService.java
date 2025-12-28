package com.example.nexus.app.mypage.service;

import com.example.nexus.app.mypage.dto.DashboardDto;
import com.example.nexus.app.mypage.dto.ProfileDto;
import com.example.nexus.app.mypage.dto.RecentlyViewedTestDto;
import com.example.nexus.app.mypage.dto.TotalParticipationDto;
import com.example.nexus.app.mypage.dto.WatchlistDto;
import com.example.nexus.app.mypage.dto.TestDeadlineDto;
import com.example.nexus.app.mypage.domain.RecentViewedPost;
import com.example.nexus.app.mypage.repository.RecentViewedPostRepository;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import com.example.nexus.app.participation.repository.ParticipationRepository;
import com.example.nexus.app.post.repository.PostLikeRepository;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.example.nexus.app.participation.domain.Participation;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final RecentViewedPostRepository recentViewedPostRepository;
    private final PostLikeRepository postLikeRepository;
    private final ParticipationRepository participationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

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
                            .thumbnailUrl(post.getThumbnailUrl())
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

        List<Participation> participated = participationRepository.findByUserIdAndStatusWithPost(userId, ParticipationStatus.APPROVED, Pageable.unpaged()).getContent();

        List<Post> participatedPosts = participated.stream().map(Participation::getPost).toList();
        int totalCount = participatedPosts.size();

        Map<String, Integer> countByCategory = participatedPosts.stream()
                .flatMap(post -> post.getMainCategory().stream())
                .collect(Collectors.groupingBy(Enum::toString, Collectors.summingInt(e -> 1)));

        return TotalParticipationDto.builder()
                .totalCount(totalCount)
                .countByCategory(countByCategory)
                .build();
    }

    public ProfileDto getProfileData(Long userId) {
        if (userId == null) {
            return ProfileDto.builder()
                    .testsUploaded(0)
                    .testsParticipating(0)
                    .testsOngoing(0)
                    .build();
        }

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return ProfileDto.builder()
                    .testsUploaded(0)
                    .testsParticipating(0)
                    .testsOngoing(0)
                    .build();
        }

        long testsUploaded = postRepository.countByCreatedBy(userId);
        long testsParticipating = participationRepository.countByUserIdAndStatus(userId, ParticipationStatus.APPROVED);

        int testsOngoing = (int) testsParticipating;

        String affiliation = postRepository.findFirstByCreatedByAndStatusOrderByCreatedAtDesc(userId, PostStatus.ACTIVE)
                .map(Post::getCreatorIntroduction)
                .orElse(null);

        return ProfileDto.builder()
                .profileImageUrl(user.getProfileUrl())
                .name(user.getNickname())
                .affiliation(affiliation)
                .testsUploaded((int) testsUploaded)
                .testsParticipating((int) testsParticipating)
                .testsOngoing(testsOngoing)
                .build();
    }
}
