package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.category.dto.response.CategoryResponse;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import com.example.nexus.app.post.domain.RewardType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record PostSummaryResponse(
        @Schema(description = "게시글 ID")
        Long id,

        @Schema(description = "제목")
        String title,

        @Schema(description = "서비스 요약")
        String serviceSummary,

        @Schema(description = "제작자 소개")
        String creatorIntroduction,

        @Schema(description = "상세 설명")
        String description,

        @Schema(description = "썸네일 URL")
        String thumbnailUrl,

        @Schema(description = "피드백 방법")
        String feedbackMethod,

        @Schema(description = "소요시간")
        String durationTime,

        @Schema(description = "참여방식")
        String participationMethod,

        @Schema(description = "QNA")
        String qna,

        @Schema(description = "리워드 타입")
        RewardType rewardType,

        @Schema(description = "최대 참여자 수")
        Integer maxParticipants,

        @Schema(description = "성별 요구사항")
        String genderRequirement,

        @Schema(description = "최소 나이")
        Integer ageMin,

        @Schema(description = "최대 나이")
        Integer ageMax,

        @Schema(description = "시작 날짜")
        LocalDateTime startDate,

        @Schema(description = "종료 날짜")
        LocalDateTime endDate,

        @Schema(description = "생성일")
        LocalDateTime createdAt,

        @Schema(description = "작성자 ID")
        Long createdBy,

        @Schema(description = "메인 카테고리")
        CategoryResponse mainCategory,

        @Schema(description = "플랫폼 카테고리")
        CategoryResponse platformCategory,

        @Schema(description = "장르 카테고리 목록")
        List<CategoryResponse> genreCategories,

        @Schema(description = "상태")
        PostStatus status,

        @Schema(description = "좋아요 수")
        Integer likeCount,

        @Schema(description = "조회수")
        Integer viewCount,

        @Schema(description = "현재 참여자 수")
        Integer currentParticipants,

        @Schema(description = "사용자 좋아요 여부")
        Boolean isLiked,

        @Schema(description = "사용자 참여 여부")
        Boolean isParticipated
) {

    public static PostSummaryResponse from(Post post, Boolean isLiked, Boolean isParticipated) {
        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getServiceSummary(),
                post.getCreatorIntroduction(),
                post.getDescription(),
                post.getThumbnailUrl(),
                post.getFeedbackMethod(),
                post.getDurationTime(),
                post.getParticipationMethod(),
                post.getQna(),
                post.getRewardType(),
                post.getMaxParticipants(),
                post.getGenderRequirement(),
                post.getAgeMin(),
                post.getAgeMax(),
                post.getStartDate(),
                post.getEndDate(),
                post.getCreatedAt(),
                post.getCreatedBy(),
                CategoryResponse.from(post.getMainCategory()),
                CategoryResponse.from(post.getPlatformCategory()),
                post.getGenreCategories().stream()
                        .map(CategoryResponse::from)
                        .toList(),
                post.getStatus(),
                post.getLikeCount(),
                post.getViewCount(),
                post.getCurrentParticipants(),
                isLiked,
                isParticipated
        );
    }
}
