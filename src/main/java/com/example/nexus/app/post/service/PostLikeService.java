package com.example.nexus.app.post.service;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.post.controller.dto.response.PostLikeToggleResponse;
import com.example.nexus.app.post.controller.dto.response.PostSummaryResponse;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostLike;
import com.example.nexus.app.post.repository.PostLikeRepository;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostLikeToggleResponse toggleLike(Long postId, Long userId) {
        Post post = getPost(postId);
        User user = getUser(userId);

        boolean isLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);

        if (isLiked) {
            postLikeRepository.deleteByUserIdAndPostId(userId, postId);
            post.decrementLikeCount();
        } else {
            PostLike postLike = PostLike.createPostLike(user, post);
            postLikeRepository.save(postLike);
            post.incrementLikeCount();
        }

        Long likeCount = postLikeRepository.countByPostId(postId);
        return PostLikeToggleResponse.of(!isLiked, likeCount);
    }

    public Page<PostSummaryResponse> findUserLike(Long userId, Pageable pageable) {
        getUser(userId);
        Page<PostLike> likes = postLikeRepository.findByUserIdWithPostPaged(userId, pageable);
        return likes.map(like -> PostSummaryResponse.from(like.getPost()));
    }

    public boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }

    public Long getLikeCount(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }
}
