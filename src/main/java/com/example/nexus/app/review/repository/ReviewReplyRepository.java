package com.example.nexus.app.review.repository;

import com.example.nexus.app.review.domain.ReviewReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 답글 Repository
 */
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {

    @Query("SELECT rr FROM ReviewReply rr JOIN FETCH rr.createdBy WHERE rr.id = :id")
    Optional<ReviewReply> findByIdWithCreatedBy(@Param("id") Long id);

    @EntityGraph(attributePaths = {"createdBy", "updatedBy"})
    @Query("SELECT rr FROM ReviewReply rr WHERE rr.review.id = :reviewId ORDER BY rr.createdAt ASC")
    List<ReviewReply> findByReviewIdOrderByCreatedAtAsc(@Param("reviewId") Long reviewId);

    @EntityGraph(attributePaths = {"createdBy", "updatedBy"})
    @Query("SELECT rr FROM ReviewReply rr WHERE rr.review.id = :reviewId ORDER BY rr.createdAt ASC")
    Page<ReviewReply> findByReviewIdOrderByCreatedAtAsc(@Param("reviewId") Long reviewId, Pageable pageable);

    @Query("SELECT rr FROM ReviewReply rr WHERE rr.createdBy.id = :userId ORDER BY rr.createdAt DESC")
    Page<ReviewReply> findByCreatedByOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    Long countByReviewId(Long reviewId);
}

