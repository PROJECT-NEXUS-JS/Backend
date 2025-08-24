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

    // 실시간 조회수 증가 (오늘 증가분만)
    public void incrementViewCount(Long postId) {
        try {
            String today = LocalDate.now().toString();
            String todayKey = TODAY_VIEW_PREFIX + postId + ":" + today;

            stringRedisTemplate.opsForValue().increment(todayKey, 1);
            stringRedisTemplate.expire(todayKey, Duration.ofHours(25));

            log.debug("조회수 증가: postId={}, key={}", postId, todayKey);
        } catch (Exception e) {
            log.error("Redis 조회수 증가 실패: postId={}", postId, e);
        }
    }

    // 오늘 증가분 조회
    public Long getTodayIncrement(Long postId) {
        try {
            String today = LocalDate.now().toString();
            String todayKey = TODAY_VIEW_PREFIX + postId + ":" + today;
            String value = stringRedisTemplate.opsForValue().get(todayKey);
            return value != null ? Long.parseLong(value) : 0L;
        } catch (Exception e) {
            log.error("오늘 증가분 조회 실패: postId={}", postId, e);
            return 0L;
        }
    }

    // 전체 누적 조회수 (DB 기준점 + 오늘 증가분)
    public Long getTotalViewCount(Long postId) {
        // DB에 저장된 누적 조회수 (어제까지의 누적)
        Long dbCumulativeCount = postRepository.findById(postId)
                .map(post -> post.getViewCount().longValue())
                .orElse(0L);

        // 오늘 증가분
        Long todayIncrement = getTodayIncrement(postId);

        return dbCumulativeCount + todayIncrement;
    }

    // 어제 누적 조회수
    public Long getYesterdayViewCount(Long postId) {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String dailyKey = DAILY_VIEW_PREFIX + postId + ":" + yesterday.toString();
            String value = stringRedisTemplate.opsForValue().get(dailyKey);

            if (value != null) {
                return Long.parseLong(value);
            }

            // Redis에 어제 데이터가 없으면 DB 값 사용 (현재 DB 값은 어제까지의 누적)
            return postRepository.findById(postId)
                    .map(post -> post.getViewCount().longValue())
                    .orElse(0L);

        } catch (Exception e) {
            log.error("어제 조회수 조회 실패: postId={}", postId, e);
            return 0L;
        }
    }

    // 일주일간 누적 조회수 (차트용)
    public List<Long> getWeeklyViewCounts(Long postId) {
        List<Long> weeklyViews = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 7일간 데이터 수집 (6일 전부터 오늘까지)
        for (int i = 6; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);
            Long cumulativeCount;

            if (i == 0) {
                // 오늘: 실시간 누적 조회수
                cumulativeCount = getTotalViewCount(postId);
            } else {
                // 과거: Redis에서 해당 날짜의 누적 조회수 조회
                String dailyKey = DAILY_VIEW_PREFIX + postId + ":" + targetDate.toString();
                String value = stringRedisTemplate.opsForValue().get(dailyKey);
                cumulativeCount = value != null ? Long.parseLong(value) : 0L;
            }

            weeklyViews.add(cumulativeCount);
        }

        return weeklyViews;
    }
}
