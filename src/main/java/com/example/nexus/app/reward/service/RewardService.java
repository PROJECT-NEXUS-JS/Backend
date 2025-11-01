package com.example.nexus.app.reward.service;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.reward.domain.ParticipantReward;
import com.example.nexus.app.reward.repository.ParticipantRewardRepository;
import com.example.nexus.notification.NotificationType;
import com.example.nexus.notification.service.NotificationService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RewardService {

    private final ParticipantRewardRepository participantRewardRepository;
    private final NotificationService notificationService;

    @Transactional
    public void payReward(Long participationId, Long userId) {
        ParticipantReward participantReward = participantRewardRepository
                .findByParticipationId(participationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPANT_REWARD_NOT_FOUND));

        Participation participation = participantReward.getParticipation();

        validatePostOwnership(participation.getPost(), userId);

        if (!participantReward.isCompleted()) {
            throw new GeneralException(ErrorStatus.NOT_COMPLETED_YET);
        }

        if (participantReward.isRewardPaid()) {
            throw new GeneralException(ErrorStatus.ALREADY_PAID);
        }

        participantReward.markAsPaid();
        participation.updatePaidStatus(LocalDateTime.now());

        notificationService.createNotification(
                participation.getUser().getId(),
                NotificationType.REWARD_PAID,
                "리워드가 지급되었습니다. 확인해보세요.",
                null
        );
    }

    private void validatePostOwnership(Post post, Long userId) {
        if (!post.isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }
    }
}
