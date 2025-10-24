package com.example.nexus.app.review.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import com.example.nexus.app.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

/**
 * 리뷰 엔티티 클래스
 * - postId와 User와 연관관계
 * - 리뷰의 평점, 내용, 생성/수정 일시, 작성자/수정자 정보 포함
 */
@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_reviews_post_id", columnList = "post_id"),
    @Index(name = "idx_reviews_created_by", columnList = "created_by"),
    @Index(name = "idx_reviews_created_by_post_id", columnList = "created_by,post_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String content;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 게시글 ID
    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 리뷰 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // 리뷰 수정자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @Builder
    public Review(Integer rating, String content, Long postId, User createdBy, User updatedBy) {
        validateRating(rating);
        validateContent(content);
        this.rating = rating;
        this.content = content;
        this.postId = postId;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public void update(Integer rating, String content, User updatedBy) {
        validateRating(rating);
        validateContent(content);
        this.rating = rating;
        this.content = content;
        this.updatedBy = updatedBy;
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1에서 5 사이의 값이어야 합니다.");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("리뷰 내용은 필수입니다.");
        }
        if (content.length() < 10) {
            throw new IllegalArgumentException("리뷰 내용은 최소 10자 이상이어야 합니다.");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("리뷰 내용은 최대 1000자까지 입력 가능합니다.");
        }
    }
} 
