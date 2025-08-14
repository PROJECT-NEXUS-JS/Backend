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

    private final ViewCountService viewCountService;
    private final StringRedisTemplate stringRedisTemplate;
    private final PostRepository postRepository;

    private static final String TODAY_VIEW_PREFIX = "view:today:";
    private static final String DAILY_VIEW_PREFIX = "view:daily:";

    // 일별: Redis 키 정리만
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void consolidateDailyViewCounts() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayStr = yesterday.toString();

        try {
            Set<String> todayViewKeys = stringRedisTemplate.keys(TODAY_VIEW_PREFIX + "*:" + yesterdayStr);

            for (String todayKey : todayViewKeys) {
                String[] parts = todayKey.split(":");
                Long postId = Long.parseLong(parts[2]);

                String viewCountStr = stringRedisTemplate.opsForValue().get(todayKey);
                Long viewCount = viewCountStr != null ? Long.parseLong(viewCountStr) : 0L;

                if (viewCount > 0) {
                    String dailyKey = DAILY_VIEW_PREFIX + postId + ":" + yesterdayStr;
                    stringRedisTemplate.opsForValue().set(dailyKey, viewCount.toString());
                    stringRedisTemplate.expire(dailyKey, Duration.ofDays(30));
                }

                stringRedisTemplate.delete(todayKey);
            }

        } catch (Exception e) {
            log.error("Redis 키 정리 중 오류 발생", e);
        }
    }

    // 주간: DB 동기화
    @Scheduled(cron = "0 0 1 * * 1")
    @Transactional
    public void weeklyDatabaseSync() {
        LocalDate lastSunday = LocalDate.now().minusDays(7);

        try {
            Set<String> allPosts = getAllPostIdsFromRedis();

            for (String postIdStr : allPosts) {
                Long postId = Long.parseLong(postIdStr);
                Long weeklyTotal = calculateWeeklyTotal(postId, lastSunday);

                if (weeklyTotal > 0) {
                    updateDatabaseViewCount(postId, weeklyTotal);
                }
            }

        } catch (Exception e) {
            log.error("주간 DB 동기화 중 오류 발생", e);
        }
    }

    private Long calculateWeeklyTotal(Long postId, LocalDate endDate) {
        Long weeklyTotal = 0L;
        for (int i = 0; i < 7; i++) {
            LocalDate date = endDate.minusDays(i);
            String dailyKey = DAILY_VIEW_PREFIX + postId + ":" + date.toString();
            String value = stringRedisTemplate.opsForValue().get(dailyKey);
            weeklyTotal += (value != null ? Long.parseLong(value) : 0L);
        }
        return weeklyTotal;
    }

    private Set<String> getAllPostIdsFromRedis() {
        // daily_view 키에서 postId 추출
        Set<String> dailyKeys = stringRedisTemplate.keys(DAILY_VIEW_PREFIX + "*");
        return dailyKeys.stream()
                .map(key -> key.split(":")[2]) // view:daily:123:2024-08-14 → 123
                .collect(Collectors.toSet());
    }

    private void updateDatabaseViewCount(Long postId, Long weeklyIncrement) {
        postRepository.findById(postId).ifPresent(post -> {
            Integer currentDbCount = post.getViewCount();
            int newDbCount = currentDbCount + weeklyIncrement.intValue();
            post.updateViewCount(newDbCount);
        });
    }
}
