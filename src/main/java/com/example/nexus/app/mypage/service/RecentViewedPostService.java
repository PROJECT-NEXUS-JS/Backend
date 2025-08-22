package com.example.nexus.app.mypage.service;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.mypage.domain.RecentViewedPost;
import com.example.nexus.app.mypage.repository.RecentViewedPostRepository;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RecentViewedPostService {

    private final RecentViewedPostRepository recentViewedPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void saveRecentView(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // 기존에 조회 기록이 있다면 삭제 후 새로 저장
        recentViewedPostRepository.findByUserIdAndPostId(userId, postId)
                .ifPresent(recentViewedPostRepository::delete);

        RecentViewedPost newRecord = RecentViewedPost.builder()
                .user(user)
                .post(post)
                .viewedAt(LocalDateTime.now())
                .build();
        recentViewedPostRepository.save(newRecord);
    }
}
