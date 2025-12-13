package com.example.nexus.app.post.service;

import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ViewCountService {

    private final StringRedisTemplate stringRedisTemplate;
    private final PostRepository postRepository;

    private static final String DAILY_VIEW_PREFIX = "view:daily:";

    // 실시간 조회수 증가 - DB 직접 업데이트
    @Transactional
    public void incrementViewCount(Long postId) {
        try {
            postRepository.incrementViewCount(postId);
        } catch (Exception e) {
            log.error("DB 조회수 증가 실패: postId={}", postId, e);
        }
    }

    // 현재 조회수 - DB에서 직접 조회
    public Long getTotalViewCount(Long postId) {
        return postRepository.findById(postId)
                .map(post -> post.getViewCount().longValue())
                .orElse(0L);
    }

    // 어제 조회수 - Redis에서 조회
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

    // 일주일간 누적 조회수 (선형 차트용)
    public List<Long> getWeeklyViewCounts(Long postId) {
        List<Long> weeklyViews = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 7일간 데이터 수집 (6일 전부터 오늘까지)
        for (int i = 6; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);
            Long cumulativeCount;

            if (i == 0) {
                // 오늘: DB에서 현재 누적 조회수 조회
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

    public Map<Long, Long> getViewCountsForPosts(List<Long> postIds) {
        return postRepository.findViewCountsByPostsIds(postIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Number) row[1]).longValue()
                ));
    }
}
