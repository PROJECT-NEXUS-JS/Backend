package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.post.domain.Post;
import lombok.Builder;
import java.util.List;

/**
 * 게시글 상세 페이지의 좌측 메인 뷰 영역 1에 필요한 정보를 담는 DTO.
 * - 서비스 등록 이미지, 한 줄 소개
 * - 프로젝트 소개(텍스트, 이미지)
 * - 신청 전 체크사항
 */
@Builder
public record PostMainViewDetailResponse(
        String serviceImage,
        String oneLineIntro,
        String projectIntroImage,
        String projectIntroText,
        List<String> checklist
) {
    public static PostMainViewDetailResponse from(Post post) {
        // 프로젝트 소개 이미지 (PostContent의 mediaUrl)
        String projectIntroImage = (post.getPostContent() != null) ? post.getPostContent().getMediaUrl() : null;

        // 프로젝트 소개 텍스트 (PostContent의 storyGuide)
        String projectIntroText = (post.getPostContent() != null) ? post.getPostContent().getStoryGuide() : null;

        // 신청 전 체크사항 (PostFeedback의 feedbackItems를 사용)
        List<String> checklist = (post.getFeedback() != null) ? post.getFeedback().getFeedbackItems() : List.of();

        return PostMainViewDetailResponse.builder()
                .serviceImage(post.getThumbnailUrl())
                .oneLineIntro(post.getServiceSummary())
                .projectIntroImage(projectIntroImage)
                .projectIntroText(projectIntroText)
                .checklist(checklist)
                .build();
    }
}
