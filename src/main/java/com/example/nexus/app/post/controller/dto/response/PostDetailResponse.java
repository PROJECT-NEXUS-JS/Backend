package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.category.dto.response.CategoryResponse;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
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

        @Schema(description = "메인 카테고리 목록")
        List<CategoryResponse> mainCategories,

        @Schema(description = "플랫폼 카테고리 목록")
        List<CategoryResponse> platformCategories,

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

        @Schema(description = "일정 정보")
        PostScheduleResponse schedule,

        @Schema(description = "참여 조건")
        PostRequirementResponse requirement,

        @Schema(description = "리워드 정보")
        PostRewardResponse reward,

        @Schema(description = "피드백 설정")
        PostFeedbackResponse feedback,

        @Schema(description = "콘텐츠 정보")
        PostContentResponse content,

        @Schema(description = "생성일")
        LocalDateTime createdAt,

        @Schema(description = "작성자 ID")
        Long createdBy,

        @Schema(description = "사용자 좋아요 여부")
        Boolean isLiked,

        @Schema(description = "사용자 참여 여부")
        Boolean isParticipated
) {
    public static PostDetailResponse from(Post post, Boolean isLiked, Boolean isParticipated) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getServiceSummary(),
                post.getCreatorIntroduction(),
                post.getDescription(),
                post.getThumbnailUrl(),
                post.getMainCategory().stream()
                        .map(CategoryResponse::from)
                        .toList(),
                post.getPlatformCategory().stream()
                        .map(CategoryResponse::from)
                        .toList(),
                post.getGenreCategories().stream()
                        .map(CategoryResponse::from)
                        .toList(),
                post.getStatus(),
                post.getLikeCount(),
                post.getViewCount(),
                post.getCurrentParticipants(),
                post.getSchedule() != null ? PostScheduleResponse.from(post.getSchedule()) : null,
                post.getRequirement() != null ? PostRequirementResponse.from(post.getRequirement()) : null,
                post.getReward() != null ? PostRewardResponse.from(post.getReward()) : null,
                post.getFeedback() != null ? PostFeedbackResponse.from(post.getFeedback()) : null,
                post.getPostContent() != null ? PostContentResponse.from(post.getPostContent()) : null,
                post.getCreatedAt(),
                post.getCreatedBy(),
                isLiked,
                isParticipated
        );
    }
}
