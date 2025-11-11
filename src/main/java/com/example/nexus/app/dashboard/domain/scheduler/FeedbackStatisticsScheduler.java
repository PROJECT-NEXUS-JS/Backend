package com.example.nexus.app.dashboard.domain.scheduler;

import com.example.nexus.app.dashboard.controller.dto.response.datacenter.DataCenterResponse;
import com.example.nexus.app.dashboard.domain.FeedbackStatisticsCache;
import com.example.nexus.app.dashboard.repository.FeedbackStatisticsCacheRepository;
import com.example.nexus.app.dashboard.repository.ParticipantFeedbackRepository;
import com.example.nexus.app.dashboard.service.DataCenterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 피드백 통계 자동 집계 스케줄러
 * - 1시간마다 모든 프로젝트의 피드백 통계를 재계산하여 캐시 업데이트
 * - 성능 최적화를 위해 미리 계산된 통계를 DB에 저장
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FeedbackStatisticsScheduler {

    private final DataCenterService dataCenterService;
    private final FeedbackStatisticsCacheRepository cacheRepository;
    private final ParticipantFeedbackRepository feedbackRepository;

    /**
     * 1시간마다 피드백 통계 자동 집계
     * - 실시간 집계가 필요하지 않은 경우 스케줄러 사용 권장
     * - 대용량 데이터 처리 시 성능 향상
     */
    @Scheduled(cron = "0 0 * * * *") // 매시 정각 (예: 01:00, 02:00, ...)
    @Transactional
    public void aggregateFeedbackStatistics() {
        log.info("피드백 통계 자동 집계 시작");

        try {
            // 피드백이 있는 모든 프로젝트 ID 조회
            List<Long> postIds = feedbackRepository.findAll().stream()
                    .map(f -> f.getPostId())
                    .distinct()
                    .toList();

            log.info("집계 대상 프로젝트 수: {}", postIds.size());

            int successCount = 0;
            int failCount = 0;

            // 각 프로젝트별로 통계 집계
            for (Long postId : postIds) {
                try {
                    aggregateForPost(postId);
                    successCount++;
                } catch (Exception e) {
                    log.error("프로젝트 {} 통계 집계 실패: {}", postId, e.getMessage(), e);
                    failCount++;
                }
            }

            log.info("피드백 통계 자동 집계 완료 - 성공: {}, 실패: {}", successCount, failCount);
        } catch (Exception e) {
            log.error("피드백 통계 자동 집계 중 오류 발생", e);
        }
    }

    /**
     * 특정 프로젝트의 통계 집계
     */
    private void aggregateForPost(Long postId) {
        // 7일, 30일, 90일 각각 집계
        int[] periods = {7, 30, 90};

        for (int days : periods) {
            try {
                // 데이터 집계
                DataCenterResponse data = dataCenterService.getDataCenterData(postId, days);

                // 캐시 업데이트 또는 생성
                FeedbackStatisticsCache cache = cacheRepository
                        .findByPostIdAndPeriodDays(postId, days)
                        .orElseGet(() -> FeedbackStatisticsCache.builder()
                                .postId(postId)
                                .periodDays(days)
                                .build());

                // 통계 업데이트
                cache.updateStatistics(
                        data.summary().totalParticipants(),
                        data.summary().averageSatisfaction(),
                        data.summary().bugOccurrenceRate(),
                        data.summary().positiveFeedbackRate(),
                        data.overallEvaluation().averageRecommendation(),
                        data.overallEvaluation().averageReuse(),
                        data.usabilityEvaluation().functionalityScore(),
                        data.usabilityEvaluation().comprehensibilityScore(),
                        data.usabilityEvaluation().loadingSpeedScore(),
                        data.usabilityEvaluation().responseTimingScore(),
                        data.usabilityEvaluation().stabilityScore(),
                        null // fullStatisticsJson은 필요시 JSON 변환하여 저장
                );

                cacheRepository.save(cache);
                log.debug("프로젝트 {} - {}일 통계 캐시 업데이트 완료", postId, days);
            } catch (Exception e) {
                log.error("프로젝트 {} - {}일 통계 집계 실패: {}", postId, days, e.getMessage());
            }
        }
    }

    /**
     * 수동 집계 트리거 (필요시 API에서 호출 가능)
     */
    @Transactional
    public void manualAggregate(Long postId) {
        log.info("프로젝트 {} 수동 통계 집계 시작", postId);
        aggregateForPost(postId);
        log.info("프로젝트 {} 수동 통계 집계 완료", postId);
    }
}

