package com.example.nexus.app.post.repository;

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
}
