package com.example.nexus.app.review.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.example.nexus.app.user.domain.User;

/**
 * 리뷰 엔티티 클래스
 * - postId와 User와 연관관계
 * - 리뷰의 평점, 내용, 생성/수정 일시, 작성자/수정자 정보 포함
 */

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 게시글 ID
    @Column(name = "post_id", nullable = false)
    private Long postId;

    //리뷰 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    //리뷰 수정자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    // 생성자, getter setter 등은 Lombok 등으로 추가 예정
} 
