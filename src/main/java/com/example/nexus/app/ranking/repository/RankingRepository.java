package com.example.nexus.app.ranking.repository;

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

public interface RankingRepository extends JpaRepository<Post, Long> {

    // 홈 화면용 랭킹 쿼리 (4개)
    
    // 오늘의 추천 (인기순 + 최신순)
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.viewCount DESC, p.createdAt DESC")
    List<Post> findTodayRecommendations(@Param("status") PostStatus status, Pageable pageable);

    // 마감 임박 (7일)
    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.endDate <= :deadline ORDER BY p.endDate ASC")
    List<Post> findDeadlineImminentForHome(@Param("status") PostStatus status, @Param("deadline") LocalDateTime deadline, Pageable pageable);

    // 인기있는 테스트
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.viewCount DESC")
    List<Post> findPopularForHome(@Param("status") PostStatus status, Pageable pageable);

    // 전체보기용 랭킹 쿼리
    
    // 인기순 랭킹 (좋아요 + 조회수 + 참여자수 + 최신순 정렬)
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.viewCount DESC, p.currentParticipants DESC, p.createdAt DESC")
    Page<Post> findPopularPosts(@Param("status") PostStatus status, Pageable pageable);

    // 최신순 랭킹
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.createdAt DESC")
    Page<Post> findRecentPosts(@Param("status") PostStatus status, Pageable pageable);

    // 마감임박순 랭킹
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.endDate ASC")
    Page<Post> findDeadlineImminentPosts(@Param("status") PostStatus status, Pageable pageable);

    // 참여자순 랭킹 (참여자수 + 최신순 정렬)
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.currentParticipants DESC, p.createdAt DESC")
    Page<Post> findParticipationCountPosts(@Param("status") PostStatus status, Pageable pageable);

    //카테고리별 필터링 쿼리
    
    // 인기순
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR p.mainCategory = :mainCategory) " +
           "AND (:platformCategory IS NULL OR p.platformCategory = :platformCategory) " +
           "ORDER BY p.likeCount DESC, p.viewCount DESC, p.currentParticipants DESC, p.createdAt DESC")
    Page<Post> findPopularPostsByCategory(@Param("status") PostStatus status,
                                         @Param("mainCategory") MainCategory mainCategory,
                                         @Param("platformCategory") PlatformCategory platformCategory,
                                         Pageable pageable);

    // 최신순
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR p.mainCategory = :mainCategory) " +
           "AND (:platformCategory IS NULL OR p.platformCategory = :platformCategory) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findRecentPostsByCategory(@Param("status") PostStatus status,
                                        @Param("mainCategory") MainCategory mainCategory,
                                        @Param("platformCategory") PlatformCategory platformCategory,
                                        Pageable pageable);

    // 마감임박순
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR p.mainCategory = :mainCategory) " +
           "AND (:platformCategory IS NULL OR p.platformCategory = :platformCategory) " +
           "ORDER BY p.endDate ASC")
    Page<Post> findDeadlineImminentPostsByCategory(@Param("status") PostStatus status,
                                                   @Param("mainCategory") MainCategory mainCategory,
                                                   @Param("platformCategory") PlatformCategory platformCategory,
                                                   Pageable pageable);

    // 참여자순
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR p.mainCategory = :mainCategory) " +
           "AND (:platformCategory IS NULL OR p.platformCategory = :platformCategory) " +
           "ORDER BY p.currentParticipants DESC, p.createdAt DESC")
    Page<Post> findParticipationCountPostsByCategory(@Param("status") PostStatus status,
                                                     @Param("mainCategory") MainCategory mainCategory,
                                                     @Param("platformCategory") PlatformCategory platformCategory,
                                                     Pageable pageable);
} 