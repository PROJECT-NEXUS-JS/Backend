package com.example.nexus.app.review.repository;

import com.example.nexus.app.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * - 게시글별 리뷰 목록 조회 등 커스텀 메서드 추가 가능
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // postId의 리뷰 목록 조회
    List<Review> findByPostId(Long postId);

    Page<Review> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    Long countByPostId(Long postId);

    Long countByPostIdAndCreatedAtBefore(Long postId, LocalDateTime dateTime);

    // 사용자가 작성한 리뷰 목록 조회
    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.createdBy " +
            "WHERE r.createdBy.id = :userId " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findByCreatedByOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    // 사용자가 특정 게시글에 작성한 리뷰 조회
    @Query("SELECT r FROM Review r " +
            "WHERE r.createdBy.id = :userId AND r.postId = :postId")
    Optional<Review> findByCreatedByAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    // 사용자가 작성한 리뷰가 있는 게시글 ID 목록 조회
    @Query("SELECT DISTINCT r.postId FROM Review r WHERE r.createdBy.id = :userId")
    List<Long> findPostIdsByCreatedBy(@Param("userId") Long userId);
}
