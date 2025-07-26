package com.example.nexus.app.post.repository;

import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByStatusOrderByCreatedAtDesc(PostStatus status, Pageable pageable);

    // 카테고리별 게시글 조회
    Page<Post> findByMainCategoryAndStatusOrderByCreatedAtDesc(MainCategory mainCategory, PostStatus status, Pageable pageable);
    
    Page<Post> findByPlatformCategoryAndStatusOrderByCreatedAtDesc(PlatformCategory platformCategory, PostStatus status, Pageable pageable);

    // 사용자가 작성한 게시글
    List<Post> findByCreatedByAndStatusOrderByCreatedAtDesc(Long userId, PostStatus status);

    // 키워드 검색 (단순 데이터 조회)
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.description LIKE %:keyword%) AND p.status = :status ORDER BY p.createdAt DESC")
    Page<Post> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") PostStatus status, Pageable pageable);

    // 상세 조회 (상태 확인 포함)
    Optional<Post> findByIdAndStatus(Long id, PostStatus status);

    // 인기 게시글 조회
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<Post> findPopularPosts(@Param("status") PostStatus status, Pageable pageable);

    // 마감임박 게시글 조회
    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.endDate > CURRENT_TIMESTAMP ORDER BY p.endDate ASC")
    Page<Post> findUpcomingDeadlinePosts(@Param("status") PostStatus status, Pageable pageable);
}
