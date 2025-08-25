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
        @Schema(description = "게시글 제목", example = "웹 프로젝트 베타테스터 모집")
        String title,

        @Schema(description = "서비스 요약", example = "AI 기반 마케팅 자동화 툴")
        String serviceSummary,

        @Schema(description = "제작자 소속 및 설명", example = "예시는 설명 5년차 풀스택 개발자입니다.")
        String creatorIntroduction,

        @Schema(description = "상세 설명", example = "새로운 마케팅 도구의 사용성을 테스트해주실 분을 찾습니다.")
        String description,

        @Schema(description = "메인 카테고리", example = "[\"WEB\"]")
        Set<MainCategory> mainCategory,

        @Schema(description = "플랫폼 카테고리", example = "[\"PC\"]")
        Set<PlatformCategory> platformCategory,

        @Schema(description = "장르 카테고리", example = "[\"MARKETING_PROMOTION\", \"AI_AUTOMATION\"]")
        Set<GenreCategory> genreCategories,

        @Schema(description = "문의 방법", example = "카카오톡 오픈채팅")
        String qnaMethod,

        @Schema(description = "시작 일시", example = "2025-08-8T10:00:00")
        LocalDateTime startDate,

        @Schema(description = "종료 일시", example = "2034-01-30T18:00:00")
        LocalDateTime endDate,

        @Schema(description = "모집 마감일", example = "2034-01-10T23:59:59")
        LocalDateTime recruitmentDeadline,

        @Schema(description = "소요 시간", example = "2주")
        String durationTime,

        @Schema(description = "최대 참여자 수", example = "10")
        Integer maxParticipants,

        @Schema(description = "성별 요구사항", example = "무관")
        String genderRequirement,

        @Schema(description = "최소 나이", example = "20")
        Integer ageMin,

        @Schema(description = "최대 나이", example = "35")
        Integer ageMax,

        @Schema(description = "추가 요구사항", example = "모바일 앱 사용 경험이 있으신 분")
        String additionalRequirements,

        @Schema(description = "리워드 타입", example = "GIFT_CARD")
        RewardType rewardType,

        @Schema(description = "리워드 설명", example = "스타벅스 기프티콘 1만원권 제공")
        String rewardDescription,

        @Schema(description = "피드백 방법", example = "구글 설문조사")
        String feedbackMethod,

        @Schema(description = "피드백 항목", example = "[\"UI/UX 전반에 대한 의견\", \"기능 개선 제안\", \"버그 리포트\"]")
        List<String> feedbackItems,

        @Schema(description = "개인정보 수집 항목", example = "[\"NAME\", \"EMAIL\", \"CONTACT\"]")
        Set<PrivacyItem> privacyItems,

        @Schema(description = "참여 방법", example = "온라인")
        String participationMethod,

        @Schema(description = "스토리 가이드", example = "자유롭게 사용해보시고 솔직한 의견을 남겨주세요.")
        String storyGuide,

        @Schema(description = "미디어 URL (파일 업로드 시 무시됨)", example = "https://example.com/demo-video")
        String mediaUrl
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
        return PostFeedback.create(post, feedbackMethod, feedbackItems, privacyItems);
    }

    public PostContent toPostContentEntity(Post post) {
        return PostContent.create(post, participationMethod, storyGuide, mediaUrl);
    }
}
