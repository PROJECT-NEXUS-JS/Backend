package com.example.nexus.app.datacenter.service;

import com.example.nexus.app.datacenter.controller.dto.response.datacenter.*;
import com.example.nexus.app.datacenter.controller.dto.response.datacenter.InsightsResponse.FeedbackItemDto;
import com.example.nexus.app.datacenter.controller.dto.response.datacenter.QualityFeedbackResponse.ProblemLocationDto;
import com.example.nexus.app.feedback.domain.Feedback;
import com.example.nexus.app.feedback.domain.BugType;
import com.example.nexus.app.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 데이터센터 서비스
 * - 참여자 피드백 데이터를 집계·분석하여 통계 제공
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DataCenterService {

    private final FeedbackRepository feedbackRepository;
    private final KeywordAnalyzer keywordAnalyzer;

    /**
     * 데이터센터 전체 데이터 조회
     *
     * @param postId 프로젝트(게시글) ID
     * @param days   최근 N일 데이터 (7, 30, 90)
     * @return 데이터센터 통합 응답
     */
    public DataCenterResponse getDataCenterData(Long postId, int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        // 기간 내 피드백 조회
        List<Feedback> feedbacks = feedbackRepository.findByPostIdAndDateRange(
                postId, startDate, endDate);

        // 전주 데이터 (비교용)
        LocalDateTime lastWeekStart = startDate.minusDays(7);
        List<Feedback> lastWeekFeedbacks = feedbackRepository.findByPostIdAndDateRange(
                postId, lastWeekStart, startDate);

        return DataCenterResponse.builder()
                .summary(buildSummary(postId, feedbacks, lastWeekFeedbacks, days))
                .overallEvaluation(buildOverallEvaluation(feedbacks))
                .qualityFeedback(buildQualityFeedback(feedbacks))
                .usabilityEvaluation(buildUsabilityEvaluation(feedbacks))
                .insights(buildInsights(feedbacks))
                .build();
    }

    /**
     * 요약 카드 데이터 생성
     */
    private DataCenterSummaryResponse buildSummary(Long postId, 
                                                   List<Feedback> feedbacks,
                                                   List<Feedback> lastWeekFeedbacks,
                                                   int days) {
        long totalParticipants = feedbacks.size();
        long lastWeekParticipants = lastWeekFeedbacks.size();
        long thisWeekParticipants = totalParticipants - lastWeekParticipants;

        // 증가율 계산
        double participantChangeRate = calculateChangeRate(totalParticipants, lastWeekParticipants);

        // 평균 만족도
        double avgSatisfaction = feedbacks.stream()
                .filter(f -> f.getOverallSatisfaction() != null)
                .mapToInt(Feedback::getOverallSatisfaction)
                .average()
                .orElse(0.0);

        double lastWeekAvgSatisfaction = lastWeekFeedbacks.stream()
                .filter(f -> f.getOverallSatisfaction() != null)
                .mapToInt(Feedback::getOverallSatisfaction)
                .average()
                .orElse(0.0);

        double satisfactionChangeRate = calculateChangeRate(avgSatisfaction, lastWeekAvgSatisfaction);

        // 버그 발생률
        long bugCount = feedbacks.stream()
                .filter(f -> Boolean.TRUE.equals(f.getHasBug()))
                .count();
        double bugRate = totalParticipants > 0 ? (bugCount * 100.0 / totalParticipants) : 0.0;

        long lastWeekBugCount = lastWeekFeedbacks.stream()
                .filter(f -> Boolean.TRUE.equals(f.getHasBug()))
                .count();
        double lastWeekBugRate = lastWeekParticipants > 0 ? 
                (lastWeekBugCount * 100.0 / lastWeekParticipants) : 0.0;
        double bugRateChangeRate = calculateChangeRate(bugRate, lastWeekBugRate);

        // 긍정 피드백 비율 (만족도 4점 이상)
        long positiveFeedbackCount = feedbacks.stream()
                .filter(f -> f.getOverallSatisfaction() != null && f.getOverallSatisfaction() >= 4)
                .count();
        double positiveFeedbackRate = totalParticipants > 0 ? 
                (positiveFeedbackCount * 100.0 / totalParticipants) : 0.0;

        long lastWeekPositiveCount = lastWeekFeedbacks.stream()
                .filter(f -> f.getOverallSatisfaction() != null && f.getOverallSatisfaction() >= 4)
                .count();
        double lastWeekPositiveRate = lastWeekParticipants > 0 ? 
                (lastWeekPositiveCount * 100.0 / lastWeekParticipants) : 0.0;
        double positiveFeedbackChangeRate = calculateChangeRate(positiveFeedbackRate, lastWeekPositiveRate);

        return DataCenterSummaryResponse.builder()
                .totalParticipants(totalParticipants)
                .participantChangeRate(participantChangeRate)
                .thisWeekParticipants(thisWeekParticipants)
                .averageSatisfaction(Math.round(avgSatisfaction * 10) / 10.0)
                .satisfactionChangeRate(satisfactionChangeRate)
                .bugOccurrenceRate(Math.round(bugRate * 10) / 10.0)
                .bugRateChangeRate(bugRateChangeRate)
                .totalFeedbacks(totalParticipants)
                .bugCount(bugCount)
                .positiveFeedbackRate(Math.round(positiveFeedbackRate * 10) / 10.0)
                .positiveFeedbackChangeRate(positiveFeedbackChangeRate)
                .positiveFeedbackCount(positiveFeedbackCount)
                .build();
    }

    /**
     * 전반 평가 데이터 생성
     */
    private OverallEvaluationResponse buildOverallEvaluation(List<Feedback> feedbacks) {
        // 평균 점수 계산
        double avgSatisfaction = calculateAverage(feedbacks, Feedback::getOverallSatisfaction);
        double avgRecommendation = calculateAverage(feedbacks, Feedback::getRecommendationIntent);
        double avgReuse = calculateAverage(feedbacks, Feedback::getReuseIntent);

        // 분포 계산
        Map<Integer, Long> satisfactionDist = calculateDistribution(feedbacks, 
                Feedback::getOverallSatisfaction);
        Map<Integer, Long> recommendationDist = calculateDistribution(feedbacks, 
                Feedback::getRecommendationIntent);
        Map<Integer, Long> reuseDist = calculateDistribution(feedbacks, 
                Feedback::getReuseIntent);

        return OverallEvaluationResponse.builder()
                .averageSatisfaction(Math.round(avgSatisfaction * 10) / 10.0)
                .averageRecommendation(Math.round(avgRecommendation * 10) / 10.0)
                .averageReuse(Math.round(avgReuse * 10) / 10.0)
                .satisfactionDistribution(satisfactionDist)
                .recommendationDistribution(recommendationDist)
                .reuseDistribution(reuseDist)
                .build();
    }

    /**
     * 품질 피드백 데이터 생성
     */
    private QualityFeedbackResponse buildQualityFeedback(List<Feedback> feedbacks) {
        // 불편 요소 Top 3 (Feedback.mostInconvenient를 문자열로 변환)
        Map<String, Long> inconvenientElements = feedbacks.stream()
                .filter(f -> f.getMostInconvenient() != null)
                .map(f -> f.getMostInconvenient().getDescription())
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // 버그 비율
        long bugCount = feedbacks.stream()
                .filter(f -> Boolean.TRUE.equals(f.getHasBug()))
                .count();
        long noBugCount = feedbacks.size() - bugCount;
        double bugRate = feedbacks.size() > 0 ? (bugCount * 100.0 / feedbacks.size()) : 0.0;

        // 만족도 점수 분포 (비율)
        Map<Integer, Double> satisfactionScoreDist = calculatePercentageDistribution(feedbacks,
                Feedback::getOverallSatisfaction);

        // 문제 유형 비중 (Feedback.bugTypes를 문자열로 변환)
        Map<String, Double> problemTypeProportions = feedbacks.stream()
                .flatMap(f -> f.getBugTypes().stream())
                .map(BugType::getDescription)
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> feedbacks.size() > 0 ? (e.getValue() * 100.0 / feedbacks.size()) : 0.0,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // 주요 문제 발생 위치 (Top 5)
        List<ProblemLocationDto> topProblemLocations = feedbacks.stream()
                .filter(f -> f.getBugLocation() != null && !f.getBugLocation().isEmpty())
                .collect(Collectors.groupingBy(
                        Feedback::getBugLocation,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    // 해당 위치의 주요 문제 유형 찾기
                    String mainProblemType = feedbacks.stream()
                            .filter(f -> e.getKey().equals(f.getBugLocation()))
                            .flatMap(f -> f.getBugTypes().stream())
                            .map(BugType::getDescription)
                            .findFirst()
                            .orElse("오류");

                    return ProblemLocationDto.builder()
                            .location(e.getKey())
                            .problemType(mainProblemType)
                            .reportCount(e.getValue())
                            .build();
                })
                .collect(Collectors.toList());

        // 스크린샷 미리보기 (최대 3개)
        List<String> screenshotPreviews = feedbacks.stream()
                .flatMap(f -> f.getScreenshotUrls().stream())
                .limit(3)
                .collect(Collectors.toList());

        return QualityFeedbackResponse.builder()
                .topInconvenientElements(inconvenientElements)
                .bugExistenceRate(Math.round(bugRate * 10) / 10.0)
                .bugExistCount(bugCount)
                .noBugCount(noBugCount)
                .satisfactionScoreDistribution(satisfactionScoreDist)
                .problemTypeProportions(problemTypeProportions)
                .topProblemLocations(topProblemLocations)
                .screenshotPreviews(screenshotPreviews)
                .build();
    }

    /**
     * 기능별 사용성 평가 데이터 생성
     */
    private UsabilityEvaluationResponse buildUsabilityEvaluation(List<Feedback> feedbacks) {
        double functionalityScore = calculateAverage(feedbacks, Feedback::getFunctionalityScore);
        double comprehensibilityScore = calculateAverage(feedbacks, Feedback::getComprehensibilityScore);
        double loadingSpeedScore = calculateAverage(feedbacks, Feedback::getSpeedScore);
        double responseTimingScore = calculateAverage(feedbacks, Feedback::getResponseTimingScore);
        // Feedback에는 stabilityScore가 없으므로 0.0으로 설정
        double stabilityScore = 0.0;

        return UsabilityEvaluationResponse.builder()
                .functionalityScore(Math.round(functionalityScore * 10) / 10.0)
                .comprehensibilityScore(Math.round(comprehensibilityScore * 10) / 10.0)
                .loadingSpeedScore(Math.round(loadingSpeedScore * 10) / 10.0)
                .responseTimingScore(Math.round(responseTimingScore * 10) / 10.0)
                .stabilityScore(Math.round(stabilityScore * 10) / 10.0)
                .build();
    }

    /**
     * 인사이트 데이터 생성 (키워드 분석 포함)
     */
    private InsightsResponse buildInsights(List<Feedback> feedbacks) {
        // 좋았던 점 피드백 (Feedback.goodPoints)
        List<FeedbackItemDto> positiveFeedbacks = feedbacks.stream()
                .filter(f -> f.getGoodPoints() != null && !f.getGoodPoints().isEmpty())
                .map(f -> FeedbackItemDto.builder()
                        .feedbackId(f.getId())
                        .summary(keywordAnalyzer.summarize(f.getGoodPoints()))
                        .fullContent(f.getGoodPoints())
                        .emoji(keywordAnalyzer.selectEmoji(f.getGoodPoints(), true))
                        .build())
                .collect(Collectors.toList());

        // 개선 제안 피드백 (Feedback.improvementSuggestions)
        List<FeedbackItemDto> improvementSuggestions = feedbacks.stream()
                .filter(f -> f.getImprovementSuggestions() != null && !f.getImprovementSuggestions().isEmpty())
                .map(f -> FeedbackItemDto.builder()
                        .feedbackId(f.getId())
                        .summary(keywordAnalyzer.summarize(f.getImprovementSuggestions()))
                        .fullContent(f.getImprovementSuggestions())
                        .emoji(keywordAnalyzer.selectEmoji(f.getImprovementSuggestions(), false))
                        .build())
                .collect(Collectors.toList());

        // 키워드 분석 (긍정 피드백 + 개선 제안 모두 분석)
        List<String> allTexts = new ArrayList<>();
        feedbacks.forEach(f -> {
            if (f.getGoodPoints() != null && !f.getGoodPoints().isEmpty()) {
                allTexts.add(f.getGoodPoints());
            }
            if (f.getImprovementSuggestions() != null && !f.getImprovementSuggestions().isEmpty()) {
                allTexts.add(f.getImprovementSuggestions());
            }
        });

        Map<String, Integer> keywords = keywordAnalyzer.extractKeywords(allTexts, 10);

        return InsightsResponse.builder()
                .positiveFeedbacks(positiveFeedbacks)
                .improvementSuggestions(improvementSuggestions)
                .keywords(keywords)
                .build();
    }

    // ============= 유틸리티 메서드 =============

    /**
     * 증가율 계산 (%)
     */
    private double calculateChangeRate(double current, double previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return Math.round(((current - previous) / previous) * 1000) / 10.0;
    }

    /**
     * 평균 계산
     */
    private double calculateAverage(List<Feedback> feedbacks,
                                    java.util.function.Function<Feedback, Integer> getter) {
        return feedbacks.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    /**
     * 분포 계산 (1~5점별 개수)
     */
    private Map<Integer, Long> calculateDistribution(List<Feedback> feedbacks,
                                                     java.util.function.Function<Feedback, Integer> getter) {
        Map<Integer, Long> distribution = feedbacks.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(score -> score, Collectors.counting()));

        // 1~5점 모두 포함 (없으면 0)
        Map<Integer, Long> result = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            result.put(i, distribution.getOrDefault(i, 0L));
        }
        return result;
    }

    /**
     * 비율 분포 계산 (%)
     */
    private Map<Integer, Double> calculatePercentageDistribution(List<Feedback> feedbacks,
                                                                 java.util.function.Function<Feedback, Integer> getter) {
        Map<Integer, Long> countDist = calculateDistribution(feedbacks, getter);
        long total = feedbacks.size();

        Map<Integer, Double> result = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            double percentage = total > 0 ? (countDist.get(i) * 100.0 / total) : 0.0;
            result.put(i, Math.round(percentage * 10) / 10.0);
        }
        return result;
    }
}

