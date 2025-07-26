package com.example.nexus.app.post.controller.dto.request;

import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.RewardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record PostCreateRequest(
        @Schema(description = "게시글 제목", example = "새로운 앱 테스트 모집")
        @NotBlank(message = "제목은 필수입니다")
        String title,

        @Schema(description = "서비스 요약", example = "음식 배달 앱 베타 테스트")
        @NotBlank(message = "서비스 요약은 필수입니다")
        String serviceSummary,

        @Schema(description = "제작자 소개", example = "스타트업 개발팀입니다")
        @NotBlank(message = "제작자 소개는 필수입니다")
        String creatorIntroduction,

        @Schema(description = "상세 설명", example = "앱의 기능을 테스트해주세요")
        @NotBlank(message = "상세 설명은 필수입니다")
        String description,

        @Schema(description = "썸네일 URL")
        String thumbnailUrl,

        @Schema(description = "피드백 방법", example = "설문조사")
        @NotBlank(message = "피드백 방법은 필수입니다")
        String feedbackMethod,

        @Schema(description = "소요시간", example = "30분")
        @NotBlank(message = "소요시간은 필수입니다")
        String durationTime,

        @Schema(description = "참여방식", example = "온라인")
        @NotBlank(message = "참여방식은 필수입니다")
        String participationMethod,

        @Schema(description = "QNA", example = "자주 묻는 질문들...")
        String qna,

        @Schema(description = "리워드 타입", example = "기프티콘")
        RewardType rewardType,

        @Schema(description = "최대 참여자 수", example = "50")
        Integer maxParticipants,

        @Schema(description = "성별 요구사항", example = "ALL")
        String genderRequirement,

        @Schema(description = "최소 나이", example = "18")
        Integer ageMin,

        @Schema(description = "최대 나이", example = "65")
        Integer ageMax,

        @Schema(description = "시작 날짜")
        @NotNull(message = "시작 날짜는 필수입니다")
        LocalDateTime startDate,

        @Schema(description = "종료 날짜")
        @NotNull(message = "종료 날짜는 필수입니다")
        LocalDateTime endDate,

        @Schema(description = "메인 카테고리")
        @NotNull(message = "메인 카테고리는 필수입니다")
        MainCategory mainCategory,

        @Schema(description = "플랫폼 카테고리")
        @NotNull(message = "플랫폼 카테고리는 필수입니다")
        PlatformCategory platformCategory,

        @Schema(description = "장르 카테고리 ID 목록 (다중선택)")
        List<Long> genreCategoryIds
) {

    public static PostCreateRequest of(String title, String serviceSummary, String creatorIntroduction,
                                       String description, String thumbnailUrl, String feedbackMethod,
                                       String durationTime, String participationMethod, String qna,
                                       RewardType rewardType, Integer maxParticipants, String genderRequirement,
                                       Integer ageMin, Integer ageMax,
                                       LocalDateTime startDate, LocalDateTime endDate,
                                       MainCategory mainCategory, PlatformCategory platformCategory, List<Long> genreCategoryIds) {
        return new PostCreateRequest(title, serviceSummary,
                creatorIntroduction, description,
                thumbnailUrl, feedbackMethod, durationTime,
                participationMethod, qna, rewardType,
                maxParticipants, genderRequirement, ageMin, ageMax,
                startDate, endDate, mainCategory, platformCategory, genreCategoryIds);
    }

    public Post toEntity() {
        return Post.builder()
                .title(this.title)
                .serviceSummary(this.serviceSummary)
                .creatorIntroduction(this.creatorIntroduction)
                .description(this.description)
                .thumbnailUrl(this.thumbnailUrl)
                .feedbackMethod(this.feedbackMethod)
                .durationTime(this.durationTime)
                .participationMethod(this.participationMethod)
                .qna(this.qna)
                .rewardType(this.rewardType)
                .maxParticipants(this.maxParticipants)
                .genderRequirement(this.genderRequirement)
                .ageMin(this.ageMin)
                .ageMax(this.ageMax)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .mainCategory(this.mainCategory)
                .platformCategory(this.platformCategory)
                .build();
    }
}
