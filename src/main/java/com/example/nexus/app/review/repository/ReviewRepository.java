package com.example.nexus.app.review.repository;

import com.example.nexus.app.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * - 게시글별 리뷰 목록 조회 등 커스텀 메서드 추가 가능
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // postId의 리뷰 목록 조회 (페이징 적용)
    Page<Review> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    // 기존 메서드 (페이징 없음)
    List<Review> findByPostId(Long postId);
    
    // postId의 리뷰 목록 조회 (평점 높은 순)
    Page<Review> findByPostIdOrderByRatingDescCreatedAtDesc(Long postId, Pageable pageable);
    
    // postId의 리뷰 목록 조회 (평점 낮은 순)
    Page<Review> findByPostIdOrderByRatingAscCreatedAtDesc(Long postId, Pageable pageable);
    

}
