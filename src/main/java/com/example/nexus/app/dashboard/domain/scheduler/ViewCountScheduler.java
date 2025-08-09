package com.example.nexus.app.dashboard.domain.scheduler;

import com.example.nexus.app.dashboard.domain.PostViewLog;
import com.example.nexus.app.dashboard.repository.PostViewLogRepository;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final PostRepository postRepository;
    private final PostViewLogRepository postViewLogRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void dailyViewCount() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Post> posts = postRepository.findAll();
        int savedCount = 0;

        for (Post post : posts) {
            if (!postViewLogRepository.existsByPostIdAndViewDate(post.getId(), yesterday)) {
                Integer viewCount = post.getViewCount();
                Long viewCountLong = (viewCount != null) ? viewCount.longValue() : 0L;
                PostViewLog log = PostViewLog.create(post.getId(), yesterday, viewCountLong);
                postViewLogRepository.save(log);
                savedCount++;
            }
        }
    }
}
