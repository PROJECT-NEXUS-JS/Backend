package com.example.nexus.app.post.service;

import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import com.example.nexus.app.participation.repository.ParticipationRepository;
import com.example.nexus.app.post.repository.PostLikeRepository;
import com.example.nexus.app.post.service.dto.PostUserStatus;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostUserStatusService {

    private final PostLikeRepository postLikeRepository;
    private final ParticipationRepository participationRepository;

    public PostUserStatus getPostUserStatus(Long postId, Long userId) {
        if (userId == null) {
            return PostUserStatus.empty();
        }

        boolean isLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);

        ParticipationStatus participationStatus = participationRepository
                .findByUserIdAndPostId(userId, postId)
                .map(Participation::getStatus)
                .orElse(null);

        boolean isParticipated = participationStatus == ParticipationStatus.APPROVED;

        return new PostUserStatus(isLiked, isParticipated, participationStatus);
    }

    public Map<Long, PostUserStatus> getPostUserStatuses(List<Long> postIds, Long userId) {
        if (userId == null || postIds.isEmpty()) {
            return postIds.stream()
                    .collect(Collectors.toMap(
                            postId -> postId,
                            postId -> PostUserStatus.empty()
                    ));
        }

        Set<Long> likedPostIds = postLikeRepository.findLikedPostIdsByUserIdAndPostIds(userId, postIds);
        Set<Long> participatedPostIds = participationRepository.findApprovedPostIdsByUserIdAndPostIds(userId, postIds);

        return postIds.stream()
                .collect(Collectors.toMap(
                        postId -> postId,
                        postId -> new PostUserStatus(
                                likedPostIds.contains(postId),
                                participatedPostIds.contains(postId),
                                null
                        )
                ));
    }
}
