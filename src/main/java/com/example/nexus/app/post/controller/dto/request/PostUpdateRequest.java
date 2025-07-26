package com.example.nexus.app.post.controller.dto.request;

import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.RewardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record PostUpdateRequest(
        @Schema(description = "게시글 제목", example = "앱 테스터 모집")
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @Schema(description = "서비스 요약", example = "수정된 음식 배달 앱 베타 테스트")
        @NotBlank(message = "서비스 요약은 필수입니다,")
        String serviceSummary,

        @Schema(description = "제작자 소개", example = "xx스타트업 개발팀입니다.")
        @NotBlank(message = "제작자 소개는 필수입니다")
        String creatorIntroduction,

        @Schema(description = "상세 설명", example = "수정된 앱의 기능을 테스트해주세요")
        @NotBlank(message = "상세 설명은 필수입니다")
        String description,

        @Schema(description = "피드백 방법", example = "수정된 설문조사")
        @NotBlank(message = "피드백 방법은 필수입니다")
        String feedbackMethod,

        @Schema(description = "소요시간", example = "1일")
        @NotBlank(message = "소요시간은 필수입니다")
        String durationTime,

        @Schema(description = "참여방식", example = "온라인")
        @NotBlank(message = "참여방식은 필수입니다")
        String participationMethod,

        @Schema(description = "QNA", example = "카톡으로 연락주세요")
        String qna,

        @Schema(description = "리워드 타입", example = "현금 지급")
        RewardType rewardType,

        @Schema(description = "최대 참여자 수", example = "100")
        Integer maxParticipants,

        @Schema(description = "성별 요구사항", example = "남성")
        String genderRequirement,

        @Schema(description = "최소 나이", example = "20")
        Integer ageMin,

        @Schema(description = "최대 나이", example = "40")
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

    public static PostUpdateRequest of(String title, String serviceSummary, String creatorIntroduction, String description,
                                       String feedbackMethod, String durationTime, String participationMethod, String qna,
                                       RewardType rewardType, Integer maxParticipants, String genderRequirement,
                                       Integer ageMin, Integer ageMax, LocalDateTime startDate, LocalDateTime endDate,
                                       MainCategory mainCategory, PlatformCategory platformCategory, List<Long> genreCategoryIds) {
        return new PostUpdateRequest(title, serviceSummary,
                creatorIntroduction, description,
                feedbackMethod, durationTime,
                participationMethod, qna, rewardType, maxParticipants, genderRequirement, ageMin, ageMax, 
                startDate, endDate, mainCategory, platformCategory, genreCategoryIds);
    }

    public void updateEntity(Post post, List<GenreCategory> genreCategories, String thumbnailUrl) {
            post.updatePost(
                    this.title,
                    this.serviceSummary,
                    this.creatorIntroduction,
                    this.description,
                    thumbnailUrl,
                    this.feedbackMethod,
                    this.durationTime,
                    this.participationMethod,
                    this.qna,
                    this.rewardType,
                    this.maxParticipants,
                    this.genderRequirement,
                    this.ageMin,
                    this.ageMax,
                    this.startDate,
                    this.endDate,
                    this.mainCategory,
                    this.platformCategory,
                    genreCategories
            );
    }
}
