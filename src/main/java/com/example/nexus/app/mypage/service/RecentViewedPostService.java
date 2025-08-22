package com.example.nexus.app.mypage.service;

import com.example.nexus.app.mypage.domain.RecentViewedPost;
import com.example.nexus.app.mypage.repository.RecentViewedPostRepository;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecentViewedPostService {

    private final RecentViewedPostRepository recentViewedPostRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveRecentView(Long userId, Post post) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found with id: " + userId)
        );

        recentViewedPostRepository.findByUserIdAndPostId(userId, post.getId())
                .ifPresentOrElse(
                        recentViewedPost -> {
                            recentViewedPostRepository.delete(recentViewedPost);
                            recentViewedPostRepository.save(RecentViewedPost.builder().user(user).post(post).viewedAt(LocalDateTime.now()).build());
                        },
                        () -> {
                            recentViewedPostRepository.save(RecentViewedPost.builder().user(user).post(post).viewedAt(LocalDateTime.now()).build());
                            // 20개 초과 시 오래된 기록 삭제
                            recentViewedPostRepository.deleteOldRecords(userId);
                        }
                );
    }
}
