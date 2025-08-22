package com.example.nexus.app.mypage.repository;

import com.example.nexus.app.mypage.domain.RecentViewedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentViewedPostRepository extends JpaRepository<RecentViewedPost, Long> {
    Optional<RecentViewedPost> findByUserIdAndPostId(Long userId, Long postId);
    List<RecentViewedPost> findByUserIdOrderByViewedAtDesc(Long userId);
    void deleteByUserIdAndPostId(Long userId, Long postId);

    @Modifying
    @Query("DELETE FROM RecentViewedPost r WHERE r.user.id = :userId AND r.id NOT IN "
            + "(SELECT r2.id FROM RecentViewedPost r2 WHERE r2.user.id = :userId ORDER BY r2.viewedAt DESC LIMIT 20)")
    void deleteOldRecords(@Param("userId") Long userId);
}
