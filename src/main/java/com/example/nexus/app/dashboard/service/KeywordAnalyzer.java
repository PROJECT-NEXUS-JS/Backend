package com.example.nexus.app.dashboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 키워드 분석 유틸리티
 * - 사전 정의된 태그 기반으로 키워드 카운팅
 */
@Component
@Slf4j
public class KeywordAnalyzer {

    // 사전 정의된 키워드 태그 (빈도수 체크용)
    private static final Map<String, List<String>> KEYWORD_TAGS = Map.ofEntries(
            Map.entry("속도", List.of("속도", "느린", "빠른", "로딩", "지연", "렉", "버벅")),
            Map.entry("디자인", List.of("디자인", "예쁜", "이쁜", "깔끔", "UI", "ui", "레이아웃", "화면")),
            Map.entry("버그", List.of("버그", "오류", "에러", "error", "bug", "문제", "이상", "안됨")),
            Map.entry("기능", List.of("기능", "작동", "실행", "동작", "사용")),
            Map.entry("편리", List.of("편리", "편한", "쉬운", "간편", "직관")),
            Map.entry("불편", List.of("불편", "어려운", "복잡", "헷갈", "이해")),
            Map.entry("성능", List.of("성능", "퍼포먼스", "최적화", "효율")),
            Map.entry("UX", List.of("UX", "ux", "사용성", "경험", "흐름")),
            Map.entry("텍스트", List.of("텍스트", "글자", "문구", "내용", "설명")),
            Map.entry("색상", List.of("색상", "색깔", "컬러", "color")),
            Map.entry("버튼", List.of("버튼", "button", "클릭")),
            Map.entry("입력", List.of("입력", "input", "폼", "form")),
            Map.entry("알림", List.of("알림", "notification", "푸시", "안내")),
            Map.entry("로그인", List.of("로그인", "login", "인증", "로그아웃"))
    );

    /**
     * 사전 정의된 태그 기반으로 키워드 카운팅
     * 
     * @param texts 분석할 텍스트 리스트
     * @param topN 상위 N개의 키워드만 반환
     * @return 키워드 태그와 빈도수 맵 (빈도수 내림차순)
     */
    public Map<String, Integer> extractKeywords(List<String> texts, int topN) {
        if (texts == null || texts.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Integer> tagFrequency = new HashMap<>();

        // 각 태그별로 빈도수 계산
        for (Map.Entry<String, List<String>> entry : KEYWORD_TAGS.entrySet()) {
            String tag = entry.getKey();
            List<String> keywords = entry.getValue();
            int count = 0;

            for (String text : texts) {
                if (text == null || text.trim().isEmpty()) {
                    continue;
                }

                String lowerText = text.toLowerCase();

                // 해당 태그의 키워드들이 텍스트에 포함되어 있는지 확인
                for (String keyword : keywords) {
                    if (lowerText.contains(keyword.toLowerCase())) {
                        count++;
                        break; // 한 텍스트에서 같은 태그는 1번만 카운트
                    }
                }
            }

            if (count > 0) {
                tagFrequency.put(tag, count);
            }
        }

        // 빈도수 기준 내림차순 정렬 후 상위 N개 반환
        return tagFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * 텍스트를 50자로 요약
     * 
     * @param text 원본 텍스트
     * @return 최대 50자로 요약된 텍스트
     */
    public String summarize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        text = text.trim();
        if (text.length() <= 50) {
            return text;
        }

        // 50자로 자르고 마지막 단어가 잘렸으면 이전 단어까지만
        String truncated = text.substring(0, 50);
        int lastSpace = truncated.lastIndexOf(' ');

        if (lastSpace > 30) { // 최소 30자는 유지
            return truncated.substring(0, lastSpace) + "...";
        }

        return truncated + "...";
    }

    /**
     * 피드백 감정에 따른 이모지 선택
     * 
     * @param text 피드백 텍스트
     * @param isPositive 긍정 피드백 여부
     * @return 적절한 이모지
     */
    public String selectEmoji(String text, boolean isPositive) {
        if (isPositive) {
            // 긍정 피드백 이모지
            if (text.contains("좋") || text.contains("훌륭") || text.contains("최고")) {
                return "❤️";
            } else if (text.contains("편리") || text.contains("쉬운")) {
                return "👍";
            } else if (text.contains("빠른") || text.contains("신속")) {
                return "⚡";
            } else if (text.contains("예쁜") || text.contains("디자인")) {
                return "✨";
            }
            return "😊";
        } else {
            // 개선 제안 이모지
            if (text.contains("버그") || text.contains("오류")) {
                return "🐛";
            } else if (text.contains("느린") || text.contains("속도")) {
                return "🐢";
            } else if (text.contains("어려운") || text.contains("복잡")) {
                return "😕";
            } else if (text.contains("디자인") || text.contains("UI")) {
                return "🎨";
            }
            return "💡";
        }
    }
}

