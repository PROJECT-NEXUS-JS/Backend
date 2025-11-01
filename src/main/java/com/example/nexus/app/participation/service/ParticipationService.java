package com.example.nexus.app.participation.service;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.participation.controller.dto.response.ParticipationSummaryResponse;
import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import com.example.nexus.app.participation.controller.dto.ParticipationApplicationDto;
import com.example.nexus.app.participation.controller.dto.request.ParticipationApplicationRequest;
import com.example.nexus.app.participation.controller.dto.response.ParticipantPrivacyResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipationResponse;
import com.example.nexus.app.participation.repository.ParticipationRepository;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PrivacyItem;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.post.service.PostUserStatusService;
import com.example.nexus.app.post.service.ViewCountService;
import com.example.nexus.app.reward.domain.ParticipantReward;
import com.example.nexus.app.reward.domain.PostReward;
import com.example.nexus.app.reward.repository.ParticipantRewardRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import com.example.nexus.notification.NotificationType;
import com.example.nexus.notification.service.NotificationService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostUserStatusService postUserStatusService;
    private final ViewCountService viewCountService;
    private final NotificationService notificationService;
    private final ParticipantRewardRepository participantRewardRepository;

    // 참가 신청
    @Transactional
    public ParticipationResponse applyForParticipation(Long postId, Long userId,
                                                       ParticipationApplicationRequest request) {
        Post post = getPostWithDetail(postId);
        User user = getUser(userId);
        validatePostForApplication(post);
        validateDuplicateApplication(postId, userId);
        validateParticipantLimit(post);

        // Controller DTO -> Domain DTO 변환
        ParticipationApplicationDto applicationDto = ParticipationApplicationDto.from(request);

        Participation participation = Participation.createApplication(post, user, applicationDto);
        Participation savedParticipation = participationRepository.save(participation);

        PostUserStatusService.PostUserStatus status = postUserStatusService.getPostUserStatus(postId, userId);
        Long currentViewCount = viewCountService.getTotalViewCount(postId);

        // 신청 알림 - 모집자에게
        notificationService.createNotification(
                post.getCreatedBy(),  // 모집자
                NotificationType.NEW_PARTICIPANT,
                "새로운 참여 신청이 도착했습니다.",
                postId.toString()
        );

        return ParticipationResponse.from(savedParticipation, status.isLiked(), status.isParticipated(),
                currentViewCount);
    }

    public Page<ParticipationSummaryResponse> getMyApplications(Long userId, String statusParam, Pageable pageable) {
        getUser(userId);

        Page<Participation> participations;

        // status가 없으면 전체 조회
        if (statusParam == null || statusParam.isEmpty()) {
            participations = participationRepository.findByUserIdWithPost(userId, pageable);
        } else if ("PAID".equalsIgnoreCase(statusParam)) {
            participations = participationRepository.findByUserIdAndStatusAndIsPaidWithPost(
                    userId, ParticipationStatus.COMPLETED, true, pageable);
        } else {
            ParticipationStatus status = ParticipationStatus.valueOf(statusParam.toUpperCase());
            participations = participationRepository.findByUserIdAndStatusWithPost(userId, status, pageable);
        }

        return participations.map(ParticipationSummaryResponse::from);
    }

    // 게시글에 대한 참가 신청자 상태별 조회
    public Page<ParticipationSummaryResponse> getPostApplications(Long postId, Long userId, String statusParam,
                                                                  Pageable pageable) {
        Post post = getPost(postId);
        validatePostOwnership(post, userId);

        Page<Participation> applications;

        if (statusParam == null || statusParam.isEmpty()) {
            applications = participationRepository.findByPostIdWithUser(postId, pageable);
        } else if ("PAID".equalsIgnoreCase(statusParam)) {
            applications = participationRepository.findByPostIdAndStatusAndIsPaidWithUser(
                    postId, ParticipationStatus.COMPLETED, true, pageable);
        } else {
            ParticipationStatus status = ParticipationStatus.valueOf(statusParam.toUpperCase());
            applications = participationRepository.findByPostIdAndStatusWithUser(postId, status, pageable);
        }

        return applications.map(ParticipationSummaryResponse::from);
    }

    public Page<ParticipantPrivacyResponse> getParticipantsPrivacyInfo(Long postId, Pageable pageable, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        if (!post.isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }

        Set<PrivacyItem> collectedItems = post.getFeedback() != null ? post.getFeedback().getPrivacyItems() : Set.of();

        Page<Participation> participations = participationRepository.findByPostId(postId, pageable);

        return participations.map(participation -> ParticipantPrivacyResponse.from(participation, collectedItems));
    }

    @Transactional
    public void approveApplication(Long participationId, Long userId) {
        Participation participation = getParticipation(participationId);
        validateParticipationOwnershipAndStatus(participation, userId);

        Post post = participation.getPost();
        participation.approve();
        post.incrementParticipants();

        // 승인 알림 - 신청자에게
        notificationService.createNotification(
                participation.getUser().getId(),  // 신청자
                NotificationType.PARTICIPATION_APPROVED,
                "참여 신청이 승인되었습니다. 테스트에 참여해보세요!",
                post.getId().toString()
        );
    }

    @Transactional
    public void rejectApplication(Long participationId, Long userId) {
        Participation participation = getParticipation(participationId);
        validateParticipationOwnershipAndStatus(participation, userId);
        participation.reject();

        // 거절 알림 - 신청자에게
        notificationService.createNotification(
                participation.getUser().getId(),  // 신청자
                NotificationType.PARTICIPATION_REJECTED,
                "참여 신청이 거절되었습니다.",
                participation.getPost().getId().toString()
        );
    }

    @Transactional
    public void cancelApplication(Long participationId, Long userId) {
        Participation participation = getParticipation(participationId);

        if (!participation.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.APPLICATION_ACCESS_DENIED);
        }

        if (!participation.isPending() && !participation.isApproved()) {
            throw new GeneralException(ErrorStatus.CANNOT_CANCEL_APPLICATION);
        }

        if (participation.isApproved()) {
            participation.getPost().decrementParticipants();
        }

        participationRepository.delete(participation);
    }

    public boolean hasApplied(Long postId, Long userId) {
        return participationRepository.existsByUserIdAndPostId(userId, postId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }

    private Post getPostWithDetail(Long postId) {
        return postRepository.findByIdWithAllDetails(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }

    private void validatePostForApplication(Post post) {
        if (!post.isActive()) {
            throw new GeneralException(ErrorStatus.POST_NOT_ACTIVE);
        }

        if (post.isExpired()) {
            throw new GeneralException(ErrorStatus.POST_EXPIRED);
        }
    }

    private void validateDuplicateApplication(Long postId, Long userId) {
        if (participationRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new GeneralException(ErrorStatus.ALREADY_APPLIED);
        }
    }

    private void validateParticipantLimit(Post post) {
        if (!post.canParticipate()) {
            throw new GeneralException(ErrorStatus.PARTICIPATION_LIMIT_EXCEEDED);
        }
    }

    private void validatePostOwnership(Post post, Long userId) {
        if (!post.isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }
    }

    private Participation getParticipation(Long participationId) {
        return participationRepository.findByIdWithUserAndPost(participationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPATION_NOT_FOUND));
    }

    private void validateParticipationOwnershipAndStatus(Participation participation, Long userId) {
        if (!participation.getPost().isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }

        if (!participation.isPending()) {
            throw new GeneralException(ErrorStatus.ALREADY_PROCESSED_APPLICATION);
        }
    }

    private Page<ParticipationResponse> mapParticipationsWithUserStatus(Page<Participation> participations,
                                                                        Long userId) {
        List<Long> postIds = participations.getContent()
                .stream()
                .map(participation -> participation.getPost().getId())
                .toList();
        Map<Long, PostUserStatusService.PostUserStatus> statusMap =
                postUserStatusService.getPostUserStatuses(postIds, userId);

        Map<Long, Long> viewCountMap = viewCountService.getViewCountsForPosts(postIds);

        return participations.map(participation -> {
            Long postId = participation.getPost().getId();
            PostUserStatusService.PostUserStatus status = statusMap.getOrDefault(postId,
                    new PostUserStatusService.PostUserStatus(false, false));
            Long currentViewCount = viewCountMap.getOrDefault(postId, 0L);
            return ParticipationResponse.from(participation, status.isLiked(), status.isParticipated(),
                    currentViewCount);
        });
    }

    // 참여자 완료 처리
    @Transactional
    public void completeParticipant(Long participationId, Long userId) {
        Participation participation = participationRepository.findByIdWithPostAndReward(participationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPATION_NOT_FOUND));

        validatePostOwnership(participation.getPost(), userId);
        participation.complete();

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
}
