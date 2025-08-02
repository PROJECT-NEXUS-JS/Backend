package com.example.nexus.app.post.controller.dto.request;

import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.post.domain.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record PostCreateRequest(
        @Schema(description = "제목")
        @NotBlank
        String title,

        @Schema(description = "서비스 요약")
        @NotBlank
        String serviceSummary,

        @Schema(description = "제작자 소개")
        @NotBlank
        String creatorIntroduction,

        @Schema(description = "상세 설명")
        @NotBlank
        String description,

        @Schema(description = "메인 카테고리")
        @NotNull
        Set<MainCategory> mainCategory,

        @Schema(description = "플랫폼 카테고리")
        @NotNull
        Set<PlatformCategory> platformCategory,

        @Schema(description = "장르 카테고리 목록")
        Set<GenreCategory> genreCategories,

        @Schema(description = "시작 날짜")
        @NotNull
        LocalDateTime startDate,

        @Schema(description = "종료 날짜")
        @NotNull
        LocalDateTime endDate,

        @Schema(description = "모집 마감일")
        LocalDateTime recruitmentDeadline,

        @Schema(description = "소요 시간")
        @NotBlank
        String durationTime,

        @Schema(description = "최대 참여자 수")
        Integer maxParticipants,

        @Schema(description = "성별 요구사항")
        String genderRequirement,

        @Schema(description = "최소 나이")
        Integer ageMin,

        @Schema(description = "최대 나이")
        Integer ageMax,

        @Schema(description = "추가 요구사항")
        String additionalRequirements,

        @Schema(description = "리워드 타입")
        RewardType rewardType,

        @Schema(description = "리워드 상세 설명")
        String rewardDescription,

        @Schema(description = "피드백 방법")
        @NotBlank
        String feedbackMethod,

        @Schema(description = "피드백 항목")
        List<String> feedbackItems,

        @Schema(description = "개인정보 수집 항목")
        String privacyCollectionItems,

        @Schema(description = "참여 방법")
        @NotBlank
        String participationMethod,

        @Schema(description = "스토리 가이드")
        String storyGuide,

        @Schema(description = "미디어 URL")
        String mediaUrl,

        @Schema(description = "Q&A 방법")
        String qnaMethod
) {
    public Post toPostEntity() {
        return toPostEntity(PostStatus.ACTIVE);
    }

    public Post toPostEntity(PostStatus status) {
        return new Post(title, serviceSummary, creatorIntroduction, description,
                null, mainCategory, platformCategory, genreCategories, status, qnaMethod);
    }

    public PostSchedule toPostScheduleEntity(Post post) {
        return PostSchedule.create(post, startDate, endDate, recruitmentDeadline, durationTime);
    }

    public PostRequirement toPostRequirementEntity(Post post) {
        return PostRequirement.create(post, maxParticipants, genderRequirement,
                ageMin, ageMax, additionalRequirements);
    }

    public PostReward toPostRewardEntity(Post post) {
        return PostReward.create(post, rewardType, rewardDescription);
    }

    public PostFeedback toPostFeedbackEntity(Post post) {
        return PostFeedback.create(post, feedbackMethod, feedbackItems, privacyCollectionItems);
    }

    public PostContent toPostContentEntity(Post post) {
        return PostContent.create(post, participationMethod, storyGuide, mediaUrl);
    }
}
