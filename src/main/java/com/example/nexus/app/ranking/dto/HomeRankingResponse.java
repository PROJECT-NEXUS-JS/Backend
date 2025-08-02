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
    private List<RankingSection> recentTests;

    @Getter
    @Builder
    public static class RankingSection {
        private Long postId;
        private String title;
        private String serviceSummary;
        private String thumbnailUrl;
        private List<String> mainCategories;
        private List<String> platformCategories;
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
        String deadlineInfo = calculateDeadlineInfo(
            post.getSchedule() != null ? post.getSchedule().getEndDate() : null
        );
        
        return RankingSection.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .serviceSummary(post.getServiceSummary())
                .thumbnailUrl(post.getThumbnailUrl())
                .mainCategories(post.getMainCategory().stream()
                        .map(category -> category.getDisplayName())
                        .toList())
                .platformCategories(post.getPlatformCategory().stream()
                        .map(category -> category.getDisplayName())
                        .toList())
                .genreCategories(post.getGenreCategories().stream()
                        .map(genre -> "#" + genre.getDisplayName())
                        .toList())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .currentParticipants(post.getCurrentParticipants())
                .maxParticipants(post.getRequirement() != null ? 
                    post.getRequirement().getMaxParticipants() : null)
                .rewardType(post.getReward() != null && post.getReward().getRewardType() != null ? 
                    post.getReward().getRewardType().name() : null)
                .deadlineInfo(deadlineInfo)
                .startDate(post.getSchedule() != null ? 
                    post.getSchedule().getStartDate() : null)
                .endDate(post.getSchedule() != null ? 
                    post.getSchedule().getEndDate() : null)
                .createdAt(post.getCreatedAt())
                .build();
    }

    private static String calculateDeadlineInfo(LocalDateTime endDate) {
        if (endDate == null) {
            return "마감일 미정";
        }
        
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
