package com.example.nexus.app.dashboard.domain.scheduler;

import com.example.nexus.app.post.service.ViewCountService;
import com.example.nexus.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisViewCountScheduler {

    private final StringRedisTemplate stringRedisTemplate;
    private final PostRepository postRepository;

    private static final String TODAY_VIEW_PREFIX = "view:today:";
    private static final String DAILY_VIEW_PREFIX = "view:daily:";

    // 매일 자정: 누적 조회수를 DB와 Redis daily에 저장
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void saveDailyCumulativeViewCounts() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayStr = yesterday.toString();

        try {
            // 어제 today 키들 조회
            Set<String> yesterdayTodayKeys = stringRedisTemplate.keys(TODAY_VIEW_PREFIX + "*:" + yesterdayStr);

            if (yesterdayTodayKeys == null || yesterdayTodayKeys.isEmpty()) {
                log.info("어제 조회수 데이터가 없습니다: {}", yesterdayStr);
                return;
            }

            for (String todayKey : yesterdayTodayKeys) {
                String[] parts = todayKey.split(":");
                if (parts.length >= 3) {
                    Long postId = Long.parseLong(parts[2]);

                    // 어제 증가분 조회
                    String incrementStr = stringRedisTemplate.opsForValue().get(todayKey);
                    Long yesterdayIncrement = incrementStr != null ? Long.parseLong(incrementStr) : 0L;

                    if (yesterdayIncrement > 0) {
                        // DB 업데이트: 기존 누적 조회수 + 어제 증가분
                        postRepository.findById(postId).ifPresent(post -> {
                            Long newCumulativeCount = post.getViewCount().longValue() + yesterdayIncrement;
                            post.updateViewCount(newCumulativeCount.intValue());
                            postRepository.save(post);

                            // Redis daily에 어제의 누적 조회수 저장
                            String dailyKey = DAILY_VIEW_PREFIX + postId + ":" + yesterdayStr;
                            stringRedisTemplate.opsForValue().set(dailyKey, newCumulativeCount.toString());
                            stringRedisTemplate.expire(dailyKey, Duration.ofDays(7));

                            log.info("누적 조회수 저장: postId={}, date={}, cumulative={}", postId, yesterdayStr, newCumulativeCount);
                        });
                    }

                    // 어제 today 키 삭제
                    stringRedisTemplate.delete(todayKey);
                }
            }

        } catch (Exception e) {
            log.error("일별 누적 조회수 저장 중 오류 발생", e);
        }
    }
}
