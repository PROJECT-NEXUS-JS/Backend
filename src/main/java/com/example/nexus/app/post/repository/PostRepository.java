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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom{

    Page<Post> findByStatusOrderByCreatedAtDesc(PostStatus status, Pageable pageable);

    // 상세 조회 (상태 확인 포함)
    Optional<Post> findByIdAndStatus(Long id, PostStatus status);

    // 사용자가 작성한 게시글
    List<Post> findByCreatedByAndStatusOrderByCreatedAtDesc(Long userId, PostStatus status);

    // ===== 실시간 랭킹 쿼리 메서드들 =====

    // 인기순 랭킹 (좋아요 + 조회수 + 참여자수 + 최신순)
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.viewCount DESC, p.currentParticipants DESC, p.createdAt DESC")
    Page<Post> findPopularPosts(@Param("status") PostStatus status, Pageable pageable);

    // 최신순 랭킹
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.createdAt DESC")
    Page<Post> findRecentPosts(@Param("status") PostStatus status, Pageable pageable);

    // 마감임박순 랭킹
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.endDate ASC")
    Page<Post> findDeadlineImminentPosts(@Param("status") PostStatus status, Pageable pageable);

    // 참여자순 랭킹
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.currentParticipants DESC, p.createdAt DESC")
    Page<Post> findParticipationCountPosts(@Param("status") PostStatus status, Pageable pageable);

    // 홈 화면용 - 오늘의 추천 (인기순 + 최신순 혼합)
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.viewCount DESC, p.createdAt DESC")
    List<Post> findTodayRecommendations(@Param("status") PostStatus status, Pageable pageable);

    // 홈 화면용 - 마감 임박 (7일 이내)
    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.endDate <= :deadline ORDER BY p.endDate ASC")
    List<Post> findDeadlineImminentForHome(@Param("status") PostStatus status, @Param("deadline") LocalDateTime deadline, Pageable pageable);

    // 홈 화면용 - 인기있는 테스트
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.viewCount DESC")
    List<Post> findPopularForHome(@Param("status") PostStatus status, Pageable pageable);

    // 카테고리별 필터링 - 인기순
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR p.mainCategory = :mainCategory) " +
           "AND (:platformCategory IS NULL OR p.platformCategory = :platformCategory) " +
           "ORDER BY p.likeCount DESC, p.viewCount DESC, p.currentParticipants DESC, p.createdAt DESC")
    Page<Post> findPopularPostsByCategory(@Param("status") PostStatus status, 
                                         @Param("mainCategory") MainCategory mainCategory,
                                         @Param("platformCategory") PlatformCategory platformCategory, 
                                         Pageable pageable);

    // 카테고리별 필터링 - 최신순
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR p.mainCategory = :mainCategory) " +
           "AND (:platformCategory IS NULL OR p.platformCategory = :platformCategory) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findRecentPostsByCategory(@Param("status") PostStatus status, 
                                        @Param("mainCategory") MainCategory mainCategory,
                                        @Param("platformCategory") PlatformCategory platformCategory, 
                                        Pageable pageable);

    // 카테고리별 필터링 - 마감임박순
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR p.mainCategory = :mainCategory) " +
           "AND (:platformCategory IS NULL OR p.platformCategory = :platformCategory) " +
           "ORDER BY p.endDate ASC")
    Page<Post> findDeadlineImminentPostsByCategory(@Param("status") PostStatus status, 
                                                   @Param("mainCategory") MainCategory mainCategory,
                                                   @Param("platformCategory") PlatformCategory platformCategory, 
                                                   Pageable pageable);

    // 카테고리별 필터링 - 참여자순
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR p.mainCategory = :mainCategory) " +
           "AND (:platformCategory IS NULL OR p.platformCategory = :platformCategory) " +
           "ORDER BY p.currentParticipants DESC, p.createdAt DESC")
    Page<Post> findParticipationCountPostsByCategory(@Param("status") PostStatus status, 
                                                     @Param("mainCategory") MainCategory mainCategory,
                                                     @Param("platformCategory") PlatformCategory platformCategory, 
                                                     Pageable pageable);
}
