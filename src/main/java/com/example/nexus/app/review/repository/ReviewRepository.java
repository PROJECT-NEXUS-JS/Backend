package com.example.nexus.app.review.repository;

import com.example.nexus.app.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * - 리뷰 엔티티 DB 접근 JPA Repository 인터페이스
 * - 게시글 별 리뷰 목록 조회 등 커스텀 메서드 추가 가능
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // postId의 리뷰 목록 조회
    List<Review> findByPostId(Long postId);
} 