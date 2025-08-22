package com.example.nexus.app.mypage.service;

import com.example.nexus.app.mypage.dto.DashboardDto;
import com.example.nexus.app.mypage.dto.RecentlyViewedTestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    // TODO: 실제 데이터 적용 필요
    public DashboardDto getDashboardData(Long userId) {
        List<RecentlyViewedTestDto> recentTests = List.of(
                RecentlyViewedTestDto.builder()
                        .postId(1L)
                        .category("모바일 앱")
                        .title("넥서스 캘린더 앱 UX/UI 테스트")
                        .oneLineIntro("새로운 캘린더 앱의 사용자 경험을 평가해주세요.")
                        .tags(List.of("#UI/UX", "#모바일", "#캘린더"))
                        .viewedAt(LocalDateTime.now().minusHours(2))
                        .build(),
                RecentlyViewedTestDto.builder()
                        .postId(2L)
                        .category("웹 서비스")
                        .title("넥서스 커뮤니티 신규 기능 테스트")
                        .oneLineIntro("게시판에 추가된 실시간 채팅 기능을 사용해보고 피드백을 주세요.")
                        .tags(List.of("#웹", "#커뮤니티", "#실시간"))
                        .viewedAt(LocalDateTime.now().minusHours(5))
                        .build(),
                RecentlyViewedTestDto.builder()
                        .postId(3L)
                        .category("게임")
                        .title("넥서스 RPG 게임 베타 테스트")
                        .oneLineIntro("새로운 판타지 세계에서 모험을 시작하세요!")
                        .tags(List.of("#게임", "#RPG", "#베타"))
                        .viewedAt(LocalDateTime.now().minusDays(1))
                        .build()
        );

        return DashboardDto.builder()
                .recentlyViewedTests(recentTests)
                .build();
    }
}
