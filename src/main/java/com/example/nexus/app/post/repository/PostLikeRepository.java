package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    @Query("SELECT pl FROM PostLike pl " +
            "JOIN FETCH pl.post p " +
            "LEFT JOIN FETCH p.schedule " +
            "LEFT JOIN FETCH p.requirement " +
            "LEFT JOIN FETCH p.reward " +
            "LEFT JOIN FETCH p.feedback " +
            "LEFT JOIN FETCH p.postContent " +
            "WHERE pl.user.id = :userId " +
            "ORDER BY pl.createdAt DESC")
    Page<PostLike> findByUserIdWithPostPaged(@Param("userId") Long userId, Pageable pageable);

    // 통계 조회용
    long countByPostId(Long postId);

    @Query("SELECT pl.post.id " +
            "FROM PostLike pl " +
            "WHERE pl.user.id = :userId AND pl.post.id IN :postIds")
    Set<Long> findLikedPostIdsByUserIdAndPostIds(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    Long countByPostIdAndCreatedAtBefore(Long postId, LocalDateTime dateTime);

    // 마이페이지 관심 목록 - 마감 임박 테스트 조회용 메서드
    @Query("SELECT p FROM PostLike pl JOIN pl.post p WHERE pl.user.id = :userId AND p.status = 'ACTIVE' AND p.schedule.recruitmentDeadline >= CURRENT_DATE() ORDER BY p.schedule.recruitmentDeadline ASC")
    List<Post> findLikedPostsWithNearingDeadline(@Param("userId") Long userId);

    @Query("SELECT DATE(pl.createdAt) as date, COUNT(pl) as count " +
            "FROM PostLike pl " +
            "WHERE pl.post.id = :postId " +
            "AND pl.createdAt >= :startDate AND pl.createdAt < :endDate " +
            "GROUP BY DATE(pl.createdAt) " +
            "ORDER BY DATE(pl.createdAt)")
    List<Object[]> countByPostIdGroupByDate(@Param("postId") Long postId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
