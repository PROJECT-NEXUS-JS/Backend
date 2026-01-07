package com.example.nexus.app.participation.service;

import com.example.nexus.app.feedback.repository.FeedbackRepository;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.participation.controller.dto.ParticipationApplicationDto;
import com.example.nexus.app.participation.controller.dto.request.ParticipantSearchRequest;
import com.example.nexus.app.participation.controller.dto.request.ParticipationApplicationRequest;
import com.example.nexus.app.participation.controller.dto.response.ParticipantDetailResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipantListResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipantPrivacyResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipationResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipationStatisticsResponse;
import com.example.nexus.app.participation.controller.dto.response.ParticipationSummaryResponse;
import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import com.example.nexus.app.participation.repository.ParticipationRepository;
import com.example.nexus.app.participation.service.dto.ParticipationStatsDto;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PrivacyItem;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.post.service.PostUserStatusService;
import com.example.nexus.app.post.service.ViewCountService;
import com.example.nexus.app.post.service.dto.PostUserStatus;
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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationService {

    private static final String SORT_DIRECTION_ASC = "ASC";
    private static final String SORT_FIELD_APPLIED_AT = "appliedAt";

    private final ParticipationRepository participationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostUserStatusService postUserStatusService;
    private final ViewCountService viewCountService;
    private final NotificationService notificationService;
    private final ParticipantRewardRepository participantRewardRepository;
    private final FeedbackRepository feedbackRepository;

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

        PostUserStatus status = postUserStatusService.getPostUserStatus(postId, userId);
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

        if (statusParam == null || statusParam.isEmpty()) {
            participations = participationRepository.findByUserIdWithPost(userId, pageable);
        } else {
            ParticipationStatus status = ParticipationStatus.valueOf(statusParam.toUpperCase());
            participations = participationRepository.findByUserIdAndStatusWithPost(userId, status, pageable);
        }

        return participations.map(ParticipationSummaryResponse::from);
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

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
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

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
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

    @Transactional
    public void completeByParticipant(Long participationId, Long userId) {
        Participation participation = getParticipation(participationId);

        validateParticipantOwnership(participation, userId);
        validateNotAlreadyFeedbackCompleted(participation);

        participation.completeTest();
    }

    // 참여자 완료 처리
    @Transactional
    public void completeParticipant(Long participationId, Long userId) {
        Participation participation = participationRepository.findByIdWithPostAndReward(participationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPATION_NOT_FOUND));

        validatePostOwnership(participation.getPost(), userId);

        if (feedbackRepository.findByParticipationId(participationId).isEmpty()) {
            throw new GeneralException(ErrorStatus.FEEDBACK_NOT_SUBMITTED);
        }

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

    // 게시글 상태별 인원 통계
    public ParticipationStatisticsResponse getPostApplicationStatistics(Long postId, Long userId) {
        Post post = getPost(postId);
        validatePostOwnership(post, userId);

        ParticipationStatsDto stats = extractParticipationStats(postId);

        return ParticipationStatisticsResponse.of(
                stats.pendingCount(),
                stats.approvedCount(),
                stats.feedbackCompletedCount(),
                stats.testCompletedCount(),
                stats.rejectedCount()
        );
    }

    // 참여자 목록 조회 (리워드 관리용)
    public Page<ParticipantListResponse> getParticipants(Long postId, ParticipantSearchRequest searchRequest,
                                                         Pageable pageable, Long userId) {
        Post post = getPost(postId);
        validatePostOwnership(post, userId);

        Pageable sortedPageable = createSortedPageable(searchRequest, pageable);
        Page<Participation> participations = findParticipationsWithFilters(postId, searchRequest, sortedPageable);

        return mapToParticipantListResponse(participations);
    }

    public ParticipantDetailResponse getParticipantDetail(Long participationId, Long userId) {
        Participation participation = getParticipation(participationId);
        validatePostOwnership(participation.getPost(), userId);

        return ParticipantDetailResponse.from(participation);
    }

    private Pageable createSortedPageable(ParticipantSearchRequest searchRequest, Pageable pageable) {
        Sort.Direction direction = getSortDirection(searchRequest);
        Sort sort = Sort.by(direction, SORT_FIELD_APPLIED_AT);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    private Sort.Direction getSortDirection(ParticipantSearchRequest searchRequest) {
        if (SORT_DIRECTION_ASC.equalsIgnoreCase(searchRequest.sortDirection())) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }

    private Page<Participation> findParticipationsWithFilters(
            Long postId,
            ParticipantSearchRequest searchRequest,
            Pageable pageable) {

        return participationRepository.findParticipantsWithFilters(
                postId,
                searchRequest.status(),
                searchRequest.searchKeyword(),
                pageable
        );
    }

    private Page<ParticipantListResponse> mapToParticipantListResponse(Page<Participation> participations) {
        return participations.map(participation ->
                ParticipantListResponse.from(participation, participation.getParticipantReward()));
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

    private void validateParticipantOwnership(Participation participation, Long userId) {
        User user = participation.getUser();
        Long participantUserId = user.getId();

        if (!participantUserId.equals(userId)) {
            throw new GeneralException(ErrorStatus.APPLICATION_ACCESS_DENIED);
        }
    }

    private void validateNotAlreadyTestCompleted(Participation participation) {
        if (participation.isTestCompleted()) {
            throw new GeneralException(ErrorStatus.PARTICIPATION_ALREADY_TEST_COMPLETED);
        }
    }

    private ParticipationStatsDto extractParticipationStats(Long postId) {
        List<Object[]> resultList = participationRepository.getParticipationStatsByPostId(postId);

        if (resultList.isEmpty()) {
            return new ParticipationStatsDto(0L, 0L, 0L, 0L, 0L);
        }

        Object[] result = resultList.get(0);

        return new ParticipationStatsDto(
                extractLongValue(result[0]),  // pendingCount
                extractLongValue(result[1]),  // approvedCount
                extractLongValue(result[2]),  // feedbackCompletedCount
                extractLongValue(result[3]),  // testCompletedCount
                extractLongValue(result[4])   // rejectedCount
        );
    }

    private Long extractLongValue(Object value) {
        return value != null ? ((Number) value).longValue() : 0L;
    }

    private void validateNotAlreadyFeedbackCompleted(Participation participation) {
        if (participation.isFeedbackCompleted() || participation.isTestCompleted()) {
            throw new GeneralException(ErrorStatus.PARTICIPATION_ALREADY_FEEDBACK_COMPLETED);
        }
    }
}
