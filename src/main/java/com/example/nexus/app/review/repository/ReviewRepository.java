package com.example.nexus.app.review.repository;

import com.example.nexus.app.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @Query("SELECT r FROM Review r JOIN FETCH r.createdBy WHERE r.id = :id")
    Optional<Review> findByIdWithCreatedBy(@Param("id") Long id);
    
    @Query("SELECT r FROM Review r JOIN FETCH r.createdBy WHERE r.postId = :postId ORDER BY r.createdAt DESC")
    List<Review> findByPostId(@Param("postId") Long postId);

    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT r FROM Review r WHERE r.postId = :postId ORDER BY r.createdAt DESC")
    Page<Review> findByPostIdOrderByCreatedAtDesc(@Param("postId") Long postId, Pageable pageable);

    Long countByPostId(Long postId);

    Long countByPostIdAndCreatedAtBefore(Long postId, LocalDateTime dateTime);

    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT r FROM Review r WHERE r.createdBy.id = :userId ORDER BY r.createdAt DESC")
    Page<Review> findByCreatedByOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.createdBy.id = :userId AND r.postId = :postId")
    Optional<Review> findByCreatedByAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT DISTINCT r.postId FROM Review r WHERE r.createdBy.id = :userId")
    List<Long> findPostIdsByCreatedBy(@Param("userId") Long userId);

    // 뱃지 시스템용 카운팅 메서드
    /**
     * 사용자가 작성한 리뷰 수 조회
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.createdBy.id = :userId")
    long countByCreatedById(@Param("userId") Long userId);

    /**
     * 기획자가 받은 총 리뷰 수 조회 (기획자가 작성한 게시글에 달린 리뷰)
     */
    @Query("SELECT COUNT(r) FROM Review r " +
           "JOIN Post p ON p.id = r.postId " +
           "WHERE p.createdBy = :creatorId")
    long countReviewsByPostCreator(@Param("creatorId") Long creatorId);
}
