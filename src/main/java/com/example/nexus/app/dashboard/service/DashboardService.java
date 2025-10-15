package com.example.nexus.app.dashboard.service;

import com.example.nexus.app.dashboard.controller.dto.request.ParticipantSearchRequest;
import com.example.nexus.app.dashboard.controller.dto.response.*;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.message.domain.Message;
import com.example.nexus.app.message.repository.MessageRepository;
import com.example.nexus.app.post.domain.*;
import com.example.nexus.app.post.repository.ParticipantRewardRepository;
import com.example.nexus.app.post.repository.ParticipationRepository;
import com.example.nexus.app.post.repository.PostLikeRepository;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.post.service.ViewCountService;
import com.example.nexus.app.review.domain.Review;
import com.example.nexus.app.review.repository.ReviewRepository;
import com.example.nexus.notification.NotificationType;
import com.example.nexus.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DashboardService {

    private final PostRepository postRepository;
    private final ParticipationRepository participationRepository;
    private final ParticipantRewardRepository participantRewardRepository;
    private final MessageRepository messageRepository;
    private final ReviewRepository reviewRepository;
    private final PostLikeRepository postLikeRepository;
    private final ViewCountService viewCountService;
    private final NotificationService notificationService;

    // 통계 카드
    public DashboardStatsResponse getDashboardStats(Long userId, Long postId) {
        validatePostOwnership(userId, postId);

        return DashboardStatsResponse.of(
                getTotalLikes(postId), getYesterdayTotalLikes(postId),
                getTotalPendingApplications(postId), getYesterdayTotalPendingApplications(postId),
                getTotalApprovedParticipants(postId), getYesterdayTotalApprovedParticipants(postId),
                getTotalReviews(postId), getYesterdayTotalReviews(postId),
                getTotalViews(postId), getYesterdayTotalViews(postId),
                getTotalUnreadMessages(postId, userId)
        );
    }

    // 빠른 액션
    public Page<WaitingParticipantResponse> getWaitingParticipants(Long userId, Long postId, Pageable pageable) {
        validatePostOwnership(userId, postId);

        Page<Participation> waitingParticipants = participationRepository.findByPostIdAndStatus(postId, ParticipationStatus.PENDING, pageable);

        return waitingParticipants.map(participation -> WaitingParticipantResponse.of(
                participation.getUser().getId(),
                participation.getUser().getNickname(),
                participation.getUser().getProfileUrl(),
                participation.getAppliedAt()
        ));
    }

    public Page<RecentMessageResponse> getRecentMessages(Long userId, Long postId, Pageable pageable) {
        validatePostOwnership(userId, postId);

        Page<Message> messages = messageRepository.findByRoomPostId(postId, pageable);

        return messages.map(message -> RecentMessageResponse.of(
                message.getRoom().getId(),
                message.getSender().getNickname(),
                message.getSender().getProfileUrl(),
                message.getContent(),
                message.getCreatedAt(),
                message.getIsRead()
        ));
    }

    public Page<RecentReviewResponse> getRecentReviews(Long userId, Long postId, Pageable pageable) {
        validatePostOwnership(userId, postId);

        Page<Review> reviews = reviewRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable);

        return reviews.map(review -> RecentReviewResponse.of(
                review.getId(),
                review.getCreatedBy().getNickname(),
                review.getCreatedBy().getProfileUrl(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt()
        ));
    }

    // 모집 상태 토글
    @Transactional
    public PostStatusResponse toggleRecruitmentStatus(Long postId, Long userId) {
        validatePostOwnership(userId, postId);

        Post post = getPost(postId);
        PostStatus currentStatus = post.getStatus();
        PostStatus newStatus = currentStatus == PostStatus.ACTIVE ? PostStatus.COMPLETED : PostStatus.ACTIVE;

        if (newStatus == PostStatus.ACTIVE) {
            post.active();
        } else {
            post.completed();
        }

        return PostStatusResponse.of(postId, newStatus);
    }

    // 분석 그래프
    public BarChartResponse getBarChartData(Long userId, Long postId) {
        validatePostOwnership(userId, postId);

        return BarChartResponse.of(
                getTotalViews(postId),
                getTotalLikes(postId),
                getTotalPendingApplications(postId),
                getTotalApprovedParticipants(postId),
                getTotalReviews(postId)
        );
    }

    public PieChartResponse getPieChartData(Long userId, Long postId) {
        validatePostOwnership(userId, postId);

        // 참여 상태별 통계
        Long pendingCount = participationRepository.countByPostIdAndStatus(postId, ParticipationStatus.PENDING);
        Long approvedCount = participationRepository.countByPostIdAndStatus(postId, ParticipationStatus.APPROVED);
        Long completedCount = participationRepository.countByPostIdAndStatus(postId, ParticipationStatus.COMPLETED);

        // 리워드 상태별 통계
        Long pendingRewards = participantRewardRepository.countByPostIdAndRewardStatus(postId, RewardStatus.PENDING);
        Long paidRewards = participantRewardRepository.countByPostIdAndRewardStatus(postId, RewardStatus.PAID);

        // 상태별 차트 데이터
        List<PieChartResponse.PieChartItem> statusItems = List.of(
                new PieChartResponse.PieChartItem("대기", pendingCount),
                new PieChartResponse.PieChartItem("승인", approvedCount),
                new PieChartResponse.PieChartItem("완료", completedCount)
        );

        // 리워드별 차트 데이터
        List<PieChartResponse.PieChartItem> rewardItems = List.of(
                new PieChartResponse.PieChartItem("지급대기", pendingRewards != null ? pendingRewards : 0L),
                new PieChartResponse.PieChartItem("지급완료", paidRewards != null ? paidRewards : 0L)
        );

        PieChartResponse.PieChartData statusChart = new PieChartResponse.PieChartData("참여 상태", statusItems);
        PieChartResponse.PieChartData rewardChart = new PieChartResponse.PieChartData("리워드 상태", rewardItems);

        return PieChartResponse.of(statusChart, rewardChart);
    }

    public LineChartResponse getLineChartData(Long userId, Long postId) {
        validatePostOwnership(userId, postId);
        Post post = getPost(postId);

        List<Long> viewsData = viewCountService.getWeeklyViewCounts(postId);

        List<LocalDate> labels = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDateTime startDate = today.minusDays(6).atStartOfDay();
        LocalDateTime endDate = today.plusDays(1).atStartOfDay();

        for (int i = 6; i >= 0 ; i--) {
            labels.add(today.minusDays(i));
        }

        // 한 번의 쿼리로 7일치 데이터 조회
        List<Object[]> likesRaw = postLikeRepository.countByPostIdGroupByDate(postId, startDate, endDate);
        List<Object[]> applicationsRaw = participationRepository.countByPostIdAndStatusGroupByDate(postId, ParticipationStatus.PENDING, startDate, endDate);

        Map<LocalDate, Long> likesMap = likesRaw.stream()
                .collect(Collectors.toMap(
                        arr -> ((java.sql.Date)arr[0]).toLocalDate(),
                        arr -> (Long) arr[1]
                ));
        Map<LocalDate, Long> applicationsMap = applicationsRaw.stream()
                .collect(Collectors.toMap(
                        arr -> ((java.sql.Date) arr[0]).toLocalDate(),
                        arr -> (Long) arr[1]
                ));

        // 7일치 데이터 생성 (없는 날은 0)
        List<Long> likesData = labels.stream()
                .map(date -> likesMap.getOrDefault(date, 0L))
                .toList();

        List<Long> applicationsData = labels.stream()
                .map(date -> applicationsMap.getOrDefault(date, 0L))
                .toList();

        return LineChartResponse.of(labels, likesData, applicationsData, viewsData);
    }

    public Page<MyPostSummaryResponse> getMyPosts(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findByCreatedByAndStatus(userId, PostStatus.ACTIVE, pageable);

        return posts.map(post -> MyPostSummaryResponse.of(
                post.getId(),
                post.getTitle(),
                post.getStatus(),
                post.getCreatedAt()
        ));
    }

    // 참여자 관리
    public Page<ParticipantListResponse> getParticipants(Long postId, ParticipantSearchRequest searchRequest,
                                                         Pageable pageable, Long userId) {
        validatePostOwnership(userId, postId);

        Page<Participation> participations = participationRepository.findParticipantsWithFilters(
                        postId, searchRequest.getStatus(), searchRequest.getRewardStatus(), searchRequest.getNickname(),
                        searchRequest.getSortBy(), searchRequest.getSortDirection(), pageable);

        List<Long> participationIds = participations.getContent()
                .stream()
                .map(Participation::getId)
                .toList();

        Map<Long, ParticipantReward> rewardMap = participantRewardRepository.findByParticipationIds(participationIds)
                .stream()
                .collect(Collectors.toMap(
                        reward -> reward.getParticipation().getId(),
                        reward -> reward
                ));

        return participations.map(participation -> {
            ParticipantReward participantReward = rewardMap.get(participation.getId());
            return ParticipantListResponse.from(participation, participantReward);
        });
    }

    public ParticipantDetailResponse getParticipantDetail(Long postId, Long participationId, Long userId) {
        validatePostOwnership(userId, postId);

        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPATION_NOT_FOUND));

        ParticipantReward participantReward = participantRewardRepository.findByParticipationId(participationId).orElse(null);

        return ParticipantDetailResponse.from(participation, participantReward);
    }

    @Transactional
    public ParticipantDetailResponse completeParticipant(Long postId, Long participationId, Long userId) {
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

        // 참여 완료 알림 - 참여자에게
        notificationService.createNotification(
                participation.getUser().getId(),  // 참여자
                NotificationType.PARTICIPATION_COMPLETED,
                "참여가 완료되었습니다. 리워드 지급을 기다려주세요.",
                null
        );

        return ParticipantDetailResponse.from(participation, participantReward);
    }

    @Transactional
    public ParticipantDetailResponse payReward(Long postId, Long participationId, Long userId) {
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

        // 리워드 지급 알림 - 참여자에게
        notificationService.createNotification(
                participation.getUser().getId(),  // 참여자
                NotificationType.REWARD_PAID,
                "리워드가 지급되었습니다. 확인해보세요.",
                null
        );

        return ParticipantDetailResponse.from(participation, participantReward);
    }

    private void validatePostOwnership(Long userId, Long postId) {
        Post post = getPost(postId);
        if (!post.isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }

    // 총계 메서드
    private Long getTotalLikes(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    private Long getTotalPendingApplications(Long postId) {
        return participationRepository.countByPostIdAndStatus(postId, ParticipationStatus.PENDING);
    }

    private Long getTotalApprovedParticipants(Long postId) {
        return participationRepository.countByPostIdAndStatus(postId, ParticipationStatus.APPROVED);
    }

    private Long getTotalReviews(Long postId) {
        return reviewRepository.countByPostId(postId);
    }

    private Long getTotalViews(Long postId) {
        Post post = getPost(postId);
        return viewCountService.getTotalViewCount(postId);
    }

    private Long getTotalUnreadMessages(Long postId, Long userId) {
        Long count = messageRepository.countUnreadMessagesByPostId(postId, userId);
        return count != null ? count : 0L;
    }

    private Long getYesterdayTotalLikes(Long postId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return postLikeRepository.countByPostIdAndCreatedAtBefore(postId, yesterday.plusDays(1).atStartOfDay());
    }

    private Long getYesterdayTotalPendingApplications(Long postId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return participationRepository.countByPostIdAndStatusAndAppliedAtBefore(postId, ParticipationStatus.PENDING, yesterday.plusDays(1).atStartOfDay());
    }

    private Long getYesterdayTotalApprovedParticipants(Long postId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return participationRepository.countByPostIdAndStatusAndApprovedAtBefore(postId, ParticipationStatus.APPROVED, yesterday.plusDays(1).atStartOfDay());
    }

    private Long getYesterdayTotalReviews(Long postId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return reviewRepository.countByPostIdAndCreatedAtBefore(postId, yesterday.plusDays(1).atStartOfDay());
    }

    private Long getYesterdayTotalViews(Long postId) {
        return viewCountService.getYesterdayViewCount(postId);
    }
}
