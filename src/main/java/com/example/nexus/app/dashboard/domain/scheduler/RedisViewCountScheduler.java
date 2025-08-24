package com.example.nexus.app.dashboard.domain.scheduler;

import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import com.example.nexus.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisViewCountScheduler {

    private final StringRedisTemplate stringRedisTemplate;
    private final PostRepository postRepository;

    private static final String DAILY_VIEW_PREFIX = "view:daily:";

    // 매일 자정: 현재 DB 조회수를 Redis에 저장 (일주일치 보관)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional(readOnly = true)
    public void saveDailyViewSnapshots() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayStr = yesterday.toString();

        try {
            // 모든 활성 게시글의 현재 DB 조회수를 Redis에 저장
            List<Post> activePosts = postRepository.findByStatus(PostStatus.ACTIVE);

            for (Post post : activePosts) {
                Long postId = post.getId();
                Long currentDbViewCount = post.getViewCount().longValue();

                // Redis에 어제 날짜로 현재 DB 조회수 저장
                String dailyKey = DAILY_VIEW_PREFIX + postId + ":" + yesterdayStr;
                stringRedisTemplate.opsForValue().set(dailyKey, currentDbViewCount.toString());
                stringRedisTemplate.expire(dailyKey, Duration.ofDays(7)); // 7일 보관

                log.info("일별 조회수 스냅샷 저장: postId={}, date={}, viewCount={}", postId, yesterdayStr, currentDbViewCount);
            }

        } catch (Exception e) {
            log.error("일별 조회수 스냅샷 저장 중 오류 발생", e);
        }
    }
}
