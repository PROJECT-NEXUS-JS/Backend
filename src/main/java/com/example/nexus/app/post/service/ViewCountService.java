package com.example.nexus.app.post.service;

import com.example.nexus.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewCountService {

    private final StringRedisTemplate stringRedisTemplate;
    private final PostRepository postRepository;

    private static final String TODAY_VIEW_PREFIX = "view:today:";
    private static final String DAILY_VIEW_PREFIX = "view:daily:";

    // 실시간 조회수 증가
    public void incrementViewCount(Long postId) {
        try {
            String today = LocalDate.now().toString();
            String todayKey = TODAY_VIEW_PREFIX + postId + ":" + today;

            stringRedisTemplate.opsForValue().increment(todayKey, 1);
            stringRedisTemplate.expire(todayKey, Duration.ofHours(25));
        } catch (Exception e) {
            log.error("Redis 조회수 증가 실패: postId={}", postId, e);
        }
    }

    // 오늘 조회수 가져오기
    public Long getTodayViewCount(Long postId) {
        try {
            String today = LocalDate.now().toString();
            String todayKey = TODAY_VIEW_PREFIX + postId + ":" + today;
            String value = stringRedisTemplate.opsForValue().get(todayKey);
            return value != null ? Long.parseLong(value) : 0L;
        } catch (Exception e) {
            log.error("오늘 조회수 조회 실패: postId={}", postId, e);
            return 0L;
        }
    }

    // 어제 조회수 가져오기
    public Long getYesterdayViewCount(Long postId) {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String dailyKey = DAILY_VIEW_PREFIX + postId + ":" + yesterday.toString();
            String value = stringRedisTemplate.opsForValue().get(dailyKey);
            return value != null ? Long.parseLong(value) : 0L;

        } catch (Exception e) {
            log.error("어제 조회수 조회 실패: postId={}", postId, e);
            return 0L;
        }
    }

    // 전체 조회수
    public Long getTotalViewCount(Long postId) {
        // DB 누적 조회수 (배치로 업데이트된 기준점)
        Long dbViewCount = postRepository.findById(postId)
                .map(post -> post.getViewCount().longValue())
                .orElse(0L);

        // 오늘 Redis 증가 값
        Long todayIncrement = getTodayViewCount(postId);

        return dbViewCount + todayIncrement;
    }

    // 특정 날짜 조회수 가져오기
    public Long getDailyViewCount(Long postId, LocalDate date) {
        try {
            String dailyKey = DAILY_VIEW_PREFIX + postId + ":" + date.toString();
            String value = stringRedisTemplate.opsForValue().get(dailyKey);
            return value != null ? Long.parseLong(value) : 0L;

        } catch (Exception e) {
            log.error("일별 조회수 조회 실패: postId={}, date={}", postId, date, e);
            return 0L;
        }
    }

    public List<Long> getWeeklyViewCounts(Long postId) {
        List<Long> weeklyViews = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 7일간 데이터 수집 (오늘부터 6일 전까지)
        for (int i = 6; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);
            Long viewCount;

            if (i == 0) {
                // 오늘: 실시간 조회수
                viewCount = getTodayViewCount(postId);
            } else {
                // 과거: 정산된 조회수
                viewCount = getDailyViewCount(postId, targetDate);
            }

            weeklyViews.add(viewCount);
        }

        return weeklyViews;
    }
}
