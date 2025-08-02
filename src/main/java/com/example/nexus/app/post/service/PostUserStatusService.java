package com.example.nexus.app.post.service;

import com.example.nexus.app.post.domain.ParticipationStatus;
import com.example.nexus.app.post.repository.ParticipationRepository;
import com.example.nexus.app.post.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostUserStatusService {

    private final PostLikeRepository postLikeRepository;
    private final ParticipationRepository participationRepository;


    public PostUserStatus getPostUserStatus(Long postId, Long userId) {
        if (userId == null){
            return new PostUserStatus(false, false);
        }

        boolean isLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        boolean isParticipate = participationRepository.existsByUserIdAndPostIdAndStatus(userId, postId,
                ParticipationStatus.APPROVED);

        return new PostUserStatus(isLiked, isParticipate);
    }

    public Map<Long, PostUserStatus> getPostUserStatuses(List<Long> postIds, Long userId) {
        if (userId == null || postIds.isEmpty()) {
            return postIds.stream()
                    .collect(Collectors.toMap(
                            postId -> postId,
                            postId -> new PostUserStatus(false, false)
                    ));
        }

        Set<Long> likedPostIds = postLikeRepository.findLikedPostIdsByUserIdAndPostIds(userId, postIds);
        Set<Long> participatedPostIds = participationRepository.findApprovedPostIdsByUserIdAndPostIds(userId, postIds);

        return postIds.stream()
                .collect(Collectors.toMap(
                        postId -> postId,
                        postId -> new PostUserStatus(
                                likedPostIds.contains(postId),
                                participatedPostIds.contains(postId)
                        )
                ));
    }


    public record PostUserStatus(Boolean isLiked, Boolean isParticipated) {

        public static PostUserStatus empty() {
            return new PostUserStatus(null, null);
        }

        public static PostUserStatus liked() {
            return new PostUserStatus(true, null);
        }

        public static PostUserStatus participated() {
            return new PostUserStatus(null, true);
        }
    }
}
