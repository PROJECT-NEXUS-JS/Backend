package com.example.nexus.app.ranking.dto;

import com.example.nexus.app.post.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Builder
public class HomeRankingResponse {

    private List<RankingSection> todayRecommendations;
    private List<RankingSection> deadlineImminent;
    private List<RankingSection> popularTests;

    @Getter
    @Builder
    public static class RankingSection {
        private Long postId;
        private String title;
        private String serviceSummary;
        private String thumbnailUrl;
        private String mainCategory;
        private String platformCategory;
        private List<String> genreCategories;
        private Integer likeCount;
        private Integer viewCount;
        private Integer currentParticipants;
        private Integer maxParticipants;
        private String rewardType;
        private String deadlineInfo;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private LocalDateTime createdAt;
    }

    public static RankingSection from(Post post) {
        String deadlineInfo = calculateDeadlineInfo(post.getEndDate());
        
        return RankingSection.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .serviceSummary(post.getServiceSummary())
                .thumbnailUrl(post.getThumbnailUrl())
                .mainCategory(post.getMainCategory().getDisplayName())
                .platformCategory(post.getPlatformCategory().getDisplayName())
                .genreCategories(post.getGenreCategories().stream()
                        .map(genre -> "#" + genre.getDisplayName())
                        .toList())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .currentParticipants(post.getCurrentParticipants())
                .maxParticipants(post.getMaxParticipants())
                .rewardType(post.getRewardType() != null ? post.getRewardType().name() : null)
                .deadlineInfo(deadlineInfo)
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private static String calculateDeadlineInfo(LocalDateTime endDate) {
        long daysUntilDeadline = ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
        
        if (daysUntilDeadline < 0) {
            return "마감됨";
        } else if (daysUntilDeadline == 0) {
            return "오늘 마감";
        } else if (daysUntilDeadline == 1) {
            return "내일 마감";
        } else {
            return "마감 D-" + daysUntilDeadline + "일전";
        }
    }
}
