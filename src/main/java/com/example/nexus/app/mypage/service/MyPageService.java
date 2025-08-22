package com.example.nexus.app.mypage.service;

import com.example.nexus.app.mypage.dto.DashboardDto;
import com.example.nexus.app.mypage.dto.RecentlyViewedTestDto;
import com.example.nexus.app.mypage.domain.RecentViewedPost;
import com.example.nexus.app.mypage.repository.RecentViewedPostRepository;
import com.example.nexus.app.post.domain.Post;
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
}
