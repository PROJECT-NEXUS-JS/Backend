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

public record PostUpdateRequest(
        @Schema(description = "게시글 제목", example = "웹 프로젝트 베타테스터 모집 (수정)")
        String title,

        @Schema(description = "서비스 요약", example = "AI 기반 마케팅 자동화 툴 v2.0")
        String serviceSummary,

        @Schema(description = "제작자 소개", example = "5년차 풀스택 개발자, React/Spring Boot 전문")
        String creatorIntroduction,

        @Schema(description = "상세 설명", example = "업데이트된 마케팅 도구의 새로운 기능을 테스트해주실 분을 찾습니다.")
        String description,

        @Schema(description = "메인 카테고리", example = "[\"WEB\", \"MOBILE\"]")
        Set<MainCategory> mainCategory,

        @Schema(description = "플랫폼 카테고리", example = "[\"PC\", \"MOBILE\"]")
        Set<PlatformCategory> platformCategory,

        @Schema(description = "장르 카테고리", example = "[\"MARKETING_PROMOTION\", \"AI_AUTOMATION\", \"PRODUCTIVITY\"]")
        Set<GenreCategory> genreCategories,

        @Schema(description = "문의 방법", example = "디스코드 채널")
        String qnaMethod,

        @Schema(description = "시작 일시", example = "2025-08-08T10:00:00")
        LocalDateTime startDate,

        @Schema(description = "종료 일시", example = "2034-02-15T18:00:00")
        LocalDateTime endDate,

        @Schema(description = "모집 마감일", example = "2034-01-25T23:59:59")
        LocalDateTime recruitmentDeadline,

        @Schema(description = "소요 시간", example = "3주")
        String durationTime,

        @Schema(description = "최대 참여자 수", example = "15")
        Integer maxParticipants,

        @Schema(description = "성별 요구사항", example = "무관")
        String genderRequirement,

        @Schema(description = "최소 나이", example = "18")
        Integer ageMin,

        @Schema(description = "최대 나이", example = "40")
        Integer ageMax,

        @Schema(description = "추가 요구사항", example = "마케팅 업무 경험자 우대")
        String additionalRequirements,

        @Schema(description = "리워드 타입", example = "CASH")
        RewardType rewardType,

        @Schema(description = "리워드 설명", example = "현금 5만원 지급 (계좌이체)")
        String rewardDescription,

        @Schema(description = "피드백 방법", example = "화상 인터뷰")
        String feedbackMethod,

        @Schema(description = "피드백 항목", example = "[\"사용성 평가\", \"기능 개선 의견\", \"추가 기능 제안\", \"전반적인 만족도\"]")
        List<String> feedbackItems,

        @Schema(description = "개인정보 수집 항목", example = "[\"NAME\", \"EMAIL\", \"CONTACT\", \"OTHER\"]")
        Set<PrivacyItem> privacyItems,

        @Schema(description = "참여 방법", example = "하이브리드 (온라인/오프라인)")
        String participationMethod,

        @Schema(description = "스토리 가이드", example = "실제 업무 환경에서 사용해보시고 상세한 피드백을 제공해주세요.")
        String storyGuide,

        @Schema(description = "미디어 URL", example = "https://example.com/updated-demo-video")
        String mediaUrl
) {

    public PostSchedule toPostScheduleEntity(Post post) {
        return PostSchedule.create(post, startDate, endDate, recruitmentDeadline, durationTime);
    }

    public PostRequirement toPostRequirementEntity(Post post) {
        return PostRequirement.create(post, maxParticipants, genderRequirement, ageMin, ageMax, additionalRequirements);
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

    public Post toPostEntity() {
        return new Post(title, serviceSummary, creatorIntroduction, description,
                null, mainCategory, platformCategory, genreCategories, qnaMethod);
    }
}
