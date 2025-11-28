package com.example.nexus.app.dashboard.service;

import com.example.nexus.app.dashboard.controller.dto.response.BarChartResponse;
import com.example.nexus.app.dashboard.controller.dto.response.DashboardStatsResponse;
import com.example.nexus.app.dashboard.controller.dto.response.LineChartResponse;
import com.example.nexus.app.dashboard.controller.dto.response.MyPostSummaryResponse;
import com.example.nexus.app.dashboard.controller.dto.response.PieChartResponse;
import com.example.nexus.app.dashboard.controller.dto.response.PostStatusResponse;
import com.example.nexus.app.dashboard.controller.dto.response.RecentMessageResponse;
import com.example.nexus.app.dashboard.controller.dto.response.RecentReviewResponse;
import com.example.nexus.app.dashboard.controller.dto.response.WaitingParticipantResponse;
import com.example.nexus.app.dashboard.service.dto.BarChartStatsDto;
import com.example.nexus.app.dashboard.service.dto.DashboardStatsDto;
import com.example.nexus.app.dashboard.service.dto.PieChartStatsDto;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.message.domain.Message;
import com.example.nexus.app.message.repository.MessageRepository;
import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import com.example.nexus.app.participation.repository.ParticipationRepository;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import com.example.nexus.app.post.repository.PostLikeRepository;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.post.service.ViewCountService;
import com.example.nexus.app.review.domain.Review;
import com.example.nexus.app.review.repository.ReviewRepository;
import com.example.nexus.app.reward.domain.RewardStatus;
import com.example.nexus.app.reward.repository.ParticipantRewardRepository;
import com.example.nexus.notification.service.NotificationService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DashboardService {

    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final String PARTICIPATION_STATUS_PENDING = "대기";
    private static final String PARTICIPATION_STATUS_APPROVED = "승인";
    private static final String PARTICIPATION_STATUS_COMPLETED = "완료";
    private static final String REWARD_STATUS_PENDING = "지급대기";
    private static final String REWARD_STATUS_PAID = "지급완료";
    private static final String CHART_TITLE_PARTICIPATION = "참여 상태";
    private static final String CHART_TITLE_REWARD = "리워드 상태";
    private static final int WEEKLY_CHART_DAYS = 7;

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

        LocalDateTime yesterday = getYesterdayDateTime();
        DashboardStatsDto stats = extractDashboardStats(postId, yesterday);

        Long totalViews = viewCountService.getTotalViewCount(postId);
        Long yesterdayViews = viewCountService.getYesterdayViewCount(postId);
        Long unreadMessages = getUnreadMessagesCount(postId, userId);

        return DashboardStatsResponse.of(
                stats.totalLikes(), stats.yesterdayLikes(),
                stats.totalPendingApplications(), stats.yesterdayPendingApplications(),
                stats.totalApprovedParticipants(), stats.yesterdayApprovedParticipants(),
                stats.totalReviews(), stats.yesterdayReviews(),
                totalViews, yesterdayViews,
                stats.totalPendingRewards(), stats.yesterdayPendingRewards(),
                unreadMessages
        );
    }

    // 빠른 액션
    public Page<WaitingParticipantResponse> getWaitingParticipants(Long userId, Long postId, Pageable pageable) {
        validatePostOwnership(userId, postId);

        Page<Participation> waitingParticipants = participationRepository.findByPostIdAndStatus(postId, ParticipationStatus.PENDING, pageable);

        return waitingParticipants.map(participation -> WaitingParticipantResponse.of(
                participation.getId(),
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

        BarChartStatsDto stats = extractBarChartStats(postId);
        Long totalViews = viewCountService.getTotalViewCount(postId);

        return BarChartResponse.of(
                totalViews,
                stats.totalLikes(),
                stats.totalPendingApplications(),
                stats.totalApprovedParticipants(),
                stats.totalReviews()
        );
    }

    public PieChartResponse getPieChartData(Long userId, Long postId) {
        validatePostOwnership(userId, postId);

        PieChartStatsDto stats = extractPieChartStats(postId);

        PieChartResponse.PieChartData statusChart = createStatusChart(stats);
        PieChartResponse.PieChartData rewardChart = createRewardChart(stats);

        return PieChartResponse.of(statusChart, rewardChart);
    }

    public LineChartResponse getLineChartData(Long userId, Long postId) {
        validatePostOwnership(userId, postId);

        List<LocalDate> labels = generateWeeklyLabels();
        LocalDateTime startDate = getWeekStartDate();
        LocalDateTime endDate = getWeekEndDate();

        List<Long> viewsData = viewCountService.getWeeklyViewCounts(postId);
        List<Long> likesData = getWeeklyLikesData(postId, labels, startDate, endDate);
        List<Long> applicationsData = getWeeklyApplicationsData(postId, labels, startDate, endDate);

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

    private LocalDateTime getYesterdayDateTime() {
        return LocalDate.now(KOREA_ZONE_ID).atStartOfDay();
    }

    private DashboardStatsDto extractDashboardStats(Long postId, LocalDateTime yesterday) {
        List<Object[]> resultList = postRepository.getDashboardStatsByPostId(postId, yesterday);

        if (resultList.isEmpty()) {
            return new DashboardStatsDto(0L, 0L, 0L, 0L,
                    0L, 0L, 0L, 0L,
                    0L, 0L);
        }

        Object[] result = resultList.get(0);

        return new DashboardStatsDto(
                extractLongValue(result[0]),
                extractLongValue(result[1]),
                extractLongValue(result[2]),
                extractLongValue(result[3]),
                extractLongValue(result[4]),
                extractLongValue(result[5]),
                extractLongValue(result[6]),
                extractLongValue(result[7]),
                extractLongValue(result[8]),
                extractLongValue(result[9])
        );
    }

    private Long extractLongValue(Object value) {
        return value != null ? ((Number) value).longValue() : 0L;
    }

    private Long getUnreadMessagesCount(Long postId, Long userId) {
        Long count = messageRepository.countUnreadMessagesByPostId(postId, userId);
        return count != null ? count : 0L;
    }

    private PieChartStatsDto extractPieChartStats(Long postId) {
        List<Object[]> resultList = participationRepository.getPieChartStatsByPostId(postId);

        if (resultList.isEmpty()) {
            return new PieChartStatsDto(0L, 0L, 0L, 0L, 0L);
        }

        Object[] result = resultList.get(0);  // 첫 번째 행 가져오기

        return new PieChartStatsDto(
                extractLongValue(result[0]),
                extractLongValue(result[1]),
                extractLongValue(result[2]),
                extractLongValue(result[3]),
                extractLongValue(result[4])
        );
    }

    private PieChartResponse.PieChartData createStatusChart(PieChartStatsDto stats) {
        List<PieChartResponse.PieChartItem> items = List.of(
                new PieChartResponse.PieChartItem(PARTICIPATION_STATUS_PENDING, stats.pendingCount()),
                new PieChartResponse.PieChartItem(PARTICIPATION_STATUS_APPROVED, stats.approvedCount()),
                new PieChartResponse.PieChartItem(PARTICIPATION_STATUS_COMPLETED, stats.completedCount())
        );
        return new PieChartResponse.PieChartData(CHART_TITLE_PARTICIPATION, items);
    }

    private PieChartResponse.PieChartData createRewardChart(PieChartStatsDto stats) {
        List<PieChartResponse.PieChartItem> items = List.of(
                new PieChartResponse.PieChartItem(REWARD_STATUS_PENDING, stats.pendingRewards()),
                new PieChartResponse.PieChartItem(REWARD_STATUS_PAID, stats.paidRewards())
        );
        return new PieChartResponse.PieChartData(CHART_TITLE_REWARD, items);
    }

    private BarChartStatsDto extractBarChartStats(Long postId) {
        List<Object[]> resultList = postRepository.getBarChartStatsByPostId(postId);

        if (resultList.isEmpty()) {
            return new BarChartStatsDto(0L, 0L, 0L, 0L);
        }

        Object[] result = resultList.get(0);

        return new BarChartStatsDto(
                extractLongValue(result[0]),
                extractLongValue(result[1]),
                extractLongValue(result[2]),
                extractLongValue(result[3])
        );
    }

    private List<LocalDate> generateWeeklyLabels() {
        LocalDate today = LocalDate.now(KOREA_ZONE_ID);
        List<LocalDate> labels = new ArrayList<>();
        for (int i = WEEKLY_CHART_DAYS - 1; i >= 0; i--) {
            labels.add(today.minusDays(i));
        }
        return labels;
    }

    private LocalDateTime getWeekStartDate() {
        return LocalDate.now(KOREA_ZONE_ID)
                .minusDays(WEEKLY_CHART_DAYS - 1)
                .atStartOfDay();
    }

    private LocalDateTime getWeekEndDate() {
        return LocalDate.now(KOREA_ZONE_ID)
                .plusDays(1)
                .atStartOfDay();
    }

    private List<Long> getWeeklyLikesData(Long postId, List<LocalDate> labels,
                                          LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> likesRaw = postLikeRepository
                .countByPostIdGroupByDate(postId, startDate, endDate);
        Map<LocalDate, Long> likesMap = convertToDateMap(likesRaw);
        return mapToDataList(labels, likesMap);
    }

    private List<Long> getWeeklyApplicationsData(Long postId, List<LocalDate> labels,
                                                 LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> applicationsRaw = participationRepository
                .countByPostIdAndStatusGroupByDate(postId, ParticipationStatus.PENDING, startDate, endDate);
        Map<LocalDate, Long> applicationsMap = convertToDateMap(applicationsRaw);
        return mapToDataList(labels, applicationsMap);
    }

    private Map<LocalDate, Long> convertToDateMap(List<Object[]> rawData) {
        return rawData.stream()
                .collect(Collectors.toMap(
                        arr -> ((java.sql.Date) arr[0]).toLocalDate(),
                        arr -> (Long) arr[1]
                ));
    }

    private List<Long> mapToDataList(List<LocalDate> labels, Map<LocalDate, Long> dataMap) {
        return labels.stream()
                .map(date -> dataMap.getOrDefault(date, 0L))
                .toList();
    }
}
