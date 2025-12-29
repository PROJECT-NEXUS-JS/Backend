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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecentViewedPostService {

    private final RecentViewedPostRepository recentViewedPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void saveRecentView(Long userId, Long postId) {
        // Pessimistic Lock을 사용하여 동시성 문제 해결
        Optional<RecentViewedPost> existingRecord =
                recentViewedPostRepository.findByUserIdAndPostIdWithLock(userId, postId);

        if (existingRecord.isPresent()) {
            int updated = recentViewedPostRepository.updateViewedAt(userId, postId, LocalDateTime.now());
            if (updated == 0) {
                throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            // 기존 레코드가 없으면 새로 생성
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

            RecentViewedPost newRecord = RecentViewedPost.builder()
                    .user(user)
                    .post(post)
                    .viewedAt(LocalDateTime.now())
                    .build();
            recentViewedPostRepository.save(newRecord);
        }
    }
    
    public void deleteByPostId(Long postId) {
        recentViewedPostRepository.deleteByPostId(postId);
    }
}
