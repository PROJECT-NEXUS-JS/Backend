package com.example.nexus.app.review.repository;

import com.example.nexus.app.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * ReviewRepository
 * - 리뷰 엔티티에 대한 DB 접근을 담당하는 JPA Repository 인터페이스
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 게시글(postId)의 리뷰 목록 조회
    List<Review> findByPostId(Long postId);

}
