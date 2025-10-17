package com.example.nexus.app.ranking.dto;

import com.example.nexus.app.post.domain.Post;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Builder
public class FullRankingResponse {

    private List<RankingItem> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    @Getter
    @Builder
    public static class RankingItem {
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
        private Integer rankingScore;
    }

    public static RankingItem from(Post post) {
        String deadlineInfo = calculateDeadlineInfo(
            post.getSchedule() != null ? post.getSchedule().getEndDate() : null
        );
        
        return RankingItem.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .serviceSummary(post.getServiceSummary())
                .thumbnailUrl(post.getThumbnailUrl())
                .mainCategories(post.getMainCategory().stream()
                        .map(category -> category.getDescription())
                        .toList())
                .platformCategories(post.getPlatformCategory().stream()
                        .map(category -> category.getDescription())
                        .toList())
                .genreCategories(post.getGenreCategories().stream()
                        .map(genre -> "#" + genre.getDescription())
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

    public static FullRankingResponse from(Page<Post> postPage) {
        List<RankingItem> content = postPage.getContent().stream()
                .map(FullRankingResponse::from)
                .toList();

        return FullRankingResponse.builder()
                .content(content)
                .pageNumber(postPage.getNumber())
                .pageSize(postPage.getSize())
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .hasNext(postPage.hasNext())
                .hasPrevious(postPage.hasPrevious())
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
