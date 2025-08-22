package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.RewardType;
import com.example.nexus.app.user.domain.User;
import lombok.Builder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 게시글 상세 페이지의 우측 사이드 뷰 영역에 필요한 정보를 담는 DTO.
 * 테스트명, 모집자명, 테스트 개요, 마감 잔여 일수, 스크랩 인원수,
 * 참가 대상, 소요 기간, 리워드 제공, 참여 방식, Q&A 방법 등을 포함합니다.
 */
@Builder
public record PostRightSidebarResponse(
        String testName,            // 게시글 제목 (Post.title)
        String recruiterName,       // 게시글 작성자 이름 (User.nickname)
        String recruiterAffiliation, // 게시글 작성자 소속 (Post.creatorIntroduction)
        String testSummary,         // 게시글 상세 설명 (Post.description)
        Long daysRemaining,         // 모집 마감일 - 현재 날짜 (PostSchedule.recruitmentDeadline)
        Integer scrapCount,         // 스크랩 인원수 (Post.likeCount)
        Integer currentParticipants, // 현재 참여자 수
        String participationTarget, // 참가 대상 (PostRequirement 조합)
        String requiredDuration,    // 소요 기간 (PostSchedule.durationTime)
        String rewardInfo,          // 리워드 제공 정보 (PostReward 조합)
        String participationMethod, // 참여 방식 (PostContent.participationMethod)
        String qnaMethod            // Q&A 방법 (Post.qnaMethod)
) {
    public static PostRightSidebarResponse from(Post post, User user) {
        // 마감 잔여 일수 계산
        Long daysRemaining = null;
        if (post.getSchedule() != null && post.getSchedule().getRecruitmentDeadline() != null) {
            daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), post.getSchedule().getRecruitmentDeadline().toLocalDate());
            if (daysRemaining < 0) { // 마감일이 지났으면 0으로 표시
                daysRemaining = 0L;
            }
        }

        // 참가 대상 정보 조합
        String participationTarget = "제한 없음";
        if (post.getRequirement() != null) {
            StringBuilder targetBuilder = new StringBuilder();
            if (post.getRequirement().getGenderRequirement() != null && !post.getRequirement().getGenderRequirement().isBlank()) {
                targetBuilder.append(post.getRequirement().getGenderRequirement());
            }
            if (post.getRequirement().getAgeMin() != null && post.getRequirement().getAgeMax() != null) {
                if (!targetBuilder.isEmpty()) targetBuilder.append(", ");
                targetBuilder.append(post.getRequirement().getAgeMin()).append("세 ~ ").append(post.getRequirement().getAgeMax()).append("세");
            } else if (post.getRequirement().getAgeMin() != null) {
                if (!targetBuilder.isEmpty()) targetBuilder.append(", ");
                targetBuilder.append(post.getRequirement().getAgeMin()).append("세 이상");
            } else if (post.getRequirement().getAgeMax() != null) {
                if (!targetBuilder.isEmpty()) targetBuilder.append(", ");
                targetBuilder.append(post.getRequirement().getAgeMax()).append("세 이하");
            }
            if (post.getRequirement().getAdditionalRequirements() != null && !post.getRequirement().getAdditionalRequirements().isBlank()) {
                if (!targetBuilder.isEmpty()) targetBuilder.append(", ");
                targetBuilder.append(post.getRequirement().getAdditionalRequirements());
            }
            if (!targetBuilder.isEmpty()) {
                participationTarget = targetBuilder.toString();
            }
        }

        // 리워드 정보 조합
        String rewardInfo = "없음";
        if (post.getReward() != null && post.getReward().getRewardType() != null) {
            if (post.getReward().getRewardType() == RewardType.NONE) {
                rewardInfo = "없음";
            } else {
                rewardInfo = post.getReward().getRewardType().getDescription();
                if (post.getReward().getRewardDescription() != null && !post.getReward().getRewardDescription().isBlank()) {
                    rewardInfo += " (" + post.getReward().getRewardDescription() + ")";
                }
            }
        }

        return PostRightSidebarResponse.builder()
                .testName(post.getTitle())
                .recruiterName(user.getNickname())
                .recruiterAffiliation(post.getCreatorIntroduction())
                .testSummary(post.getDescription())
                .daysRemaining(daysRemaining)
                .scrapCount(post.getLikeCount())
                .currentParticipants(post.getCurrentParticipants() != null ? post.getCurrentParticipants() : 0)
                .participationTarget(participationTarget)
                .requiredDuration(post.getSchedule() != null ? post.getSchedule().getDurationTime() : null)
                .rewardInfo(rewardInfo)
                .participationMethod(post.getPostContent() != null ? post.getPostContent().getParticipationMethod() : null)
                .qnaMethod(post.getQnaMethod())
                .build();
    }
}
