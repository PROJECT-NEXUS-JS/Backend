package com.example.nexus.app.reward.service;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.notification.NotificationType;
import com.example.nexus.notification.service.NotificationService;
import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.repository.ParticipationRepository;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.reward.domain.ParticipantReward;
import com.example.nexus.app.reward.domain.PostReward;
import com.example.nexus.app.reward.repository.ParticipantRewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RewardService {

    private final ParticipantRewardRepository participantRewardRepository;
    private final ParticipationRepository participationRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    @Transactional
    public void completeParticipant(Long postId, Long participationId, Long userId) {
        validatePostOwnership(userId, postId);

        Participation participation = participationRepository.findByIdWithPostAndReward(participationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPATION_NOT_FOUND));

        PostReward postReward = participation.getPost().getReward();

        if (postReward == null) {
            throw new GeneralException(ErrorStatus.POST_REWARD_NOT_FOUND);
        }

        ParticipantReward participantReward = participantRewardRepository.findByParticipationId(participationId)
                .orElseGet(() -> {
                    ParticipantReward newReward = ParticipantReward.create(participation, postReward);
                    return participantRewardRepository.save(newReward);
                });

        if (participantReward.isCompleted()) {
            throw new GeneralException(ErrorStatus.ALREADY_COMPLETED);
        }

        participantReward.markAsCompleted();

        notificationService.createNotification(
                participation.getUser().getId(),
                NotificationType.PARTICIPATION_COMPLETED,
                "참여가 완료되었습니다. 리워드 지급을 기다려주세요.",
                null
        );
    }

    @Transactional
    public void payReward(Long postId, Long participationId, Long userId) {
        validatePostOwnership(userId, postId);

        ParticipantReward participantReward = participantRewardRepository
                .findByParticipationId(participationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPANT_REWARD_NOT_FOUND));

        if (!participantReward.isCompleted()) {
            throw new GeneralException(ErrorStatus.NOT_COMPLETED_YET);
        }

        if (participantReward.isRewardPaid()) {
            throw new GeneralException(ErrorStatus.ALREADY_PAID);
        }

        participantReward.markAsPaid();

        Participation participation = participantReward.getParticipation();

        notificationService.createNotification(
                participation.getUser().getId(),
                NotificationType.REWARD_PAID,
                "리워드가 지급되었습니다. 확인해보세요.",
                null
        );
    }

    private void validatePostOwnership(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        if (!post.isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }
    }
}
