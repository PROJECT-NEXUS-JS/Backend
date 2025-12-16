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
 * 리뷰 답글 엔티티 클래스
 */
@Entity
@Table(name = "review_replies", indexes = {
    @Index(name = "idx_review_replies_review_id", columnList = "review_id"),
    @Index(name = "idx_review_replies_created_by", columnList = "created_by"),
    @Index(name = "idx_review_replies_review_id_created_at", columnList = "review_id,created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 리뷰와의 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    // 답글 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // 답글 수정자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @Builder
    public ReviewReply(String content, Review review, User createdBy, User updatedBy) {
        validateContent(content);
        this.content = content;
        this.review = review;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public void update(String content, User updatedBy) {
        validateContent(content);
        this.content = content;
        this.updatedBy = updatedBy;
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("답글 내용은 필수입니다.");
        }
        if (content.length() < 1) {
            throw new IllegalArgumentException("답글 내용은 최소 1자 이상이어야 합니다.");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("답글 내용은 최대 1000자까지 입력 가능합니다.");
        }
    }
}

