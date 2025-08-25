package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.PostContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostContentRepository extends JpaRepository<PostContent, Long> {

    Optional<PostContent> findByPostId(Long postId);

    List<PostContent> findByParticipationMethod(String participationMethod);

    @Query("SELECT pc " +
            "FROM PostContent pc " +
            "WHERE pc.storyGuide IS NOT NULL AND pc.storyGuide != ''")
    List<PostContent> findWithStoryGuide();

    @Query("SELECT pc " +
            "FROM PostContent pc " +
            "WHERE SIZE(pc.mediaUrls) > 0")
    List<PostContent> findWithMediaUrl();
}
