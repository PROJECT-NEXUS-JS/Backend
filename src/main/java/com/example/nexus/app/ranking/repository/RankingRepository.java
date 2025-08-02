package com.example.nexus.app.ranking.repository;

import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface RankingRepository extends JpaRepository<Post, Long> {

    // 홈 화면용 랭킹 쿼리 (4개)
    
    // 오늘의 추천 (인기순 + 최신순 + 가나다순)
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.viewCount DESC, p.createdAt DESC, p.title ASC")
    List<Post> findTodayRecommendations(@Param("status") PostStatus status, Pageable pageable);

    // 개인화 추천 (사용자 관심사 기반 + 마감일 적게 남은 순 + 가나다순)
    @Query("SELECT p FROM Post p JOIN p.schedule s WHERE p.status = :status " +
           "AND (:mainCategories IS NULL OR EXISTS (SELECT mc FROM p.mainCategory mc WHERE mc IN :mainCategories)) " +
           "AND (:platformCategories IS NULL OR EXISTS (SELECT pc FROM p.platformCategory pc WHERE pc IN :platformCategories)) " +
           "AND (:genreCategories IS NULL OR EXISTS (SELECT gc FROM p.genreCategories gc WHERE gc IN :genreCategories)) " +
           "ORDER BY s.endDate ASC, p.title ASC")
    List<Post> findPersonalizedRecommendations(@Param("status") PostStatus status,
                                              @Param("mainCategories") Set<MainCategory> mainCategories,
                                              @Param("platformCategories") Set<PlatformCategory> platformCategories,
                                              @Param("genreCategories") Set<GenreCategory> genreCategories,
                                              Pageable pageable);

    // 마감 임박 (7일) - PostSchedule과 JOIN + 가나다순
    @Query("SELECT p FROM Post p JOIN p.schedule s WHERE p.status = :status AND s.endDate <= :deadline ORDER BY s.endDate ASC, p.title ASC")
    List<Post> findDeadlineImminentForHome(@Param("status") PostStatus status, @Param("deadline") LocalDateTime deadline, Pageable pageable);

    // 인기있는 테스트 + 가나다순
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.viewCount DESC, p.title ASC")
    List<Post> findPopularForHome(@Param("status") PostStatus status, Pageable pageable);

    // 방금 등록한 테스트 (최신순 + 가나다순)
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.createdAt DESC, p.title ASC")
    List<Post> findRecentTestsForHome(@Param("status") PostStatus status, Pageable pageable);

    // 전체보기용 랭킹 쿼리
    
    // 인기순 랭킹 (좋아요 + 조회수 + 참여자수 + 최신순 + 가나다순 정렬)
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.viewCount DESC, p.currentParticipants DESC, p.createdAt DESC, p.title ASC")
    Page<Post> findPopularPosts(@Param("status") PostStatus status, Pageable pageable);

    // 최신순 랭킹 + 가나다순
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.createdAt DESC, p.title ASC")
    Page<Post> findRecentPosts(@Param("status") PostStatus status, Pageable pageable);

    // 마감임박순 랭킹 - PostSchedule + JOIN + 가나다순
    @Query("SELECT p FROM Post p JOIN p.schedule s WHERE p.status = :status ORDER BY s.endDate ASC, p.title ASC")
    Page<Post> findDeadlineImminentPosts(@Param("status") PostStatus status, Pageable pageable);

    // 참여자순 랭킹 (참여자수 + 최신순 + 가나다순 정렬)
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.currentParticipants DESC, p.createdAt DESC, p.title ASC")
    Page<Post> findParticipationCountPosts(@Param("status") PostStatus status, Pageable pageable);

    //카테고리별 필터링 쿼리
    
    // 인기순 + 가나다순
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR :mainCategory MEMBER OF p.mainCategory) " +
           "AND (:platformCategory IS NULL OR :platformCategory MEMBER OF p.platformCategory) " +
           "ORDER BY p.likeCount DESC, p.viewCount DESC, p.currentParticipants DESC, p.createdAt DESC, p.title ASC")
    Page<Post> findPopularPostsByCategory(@Param("status") PostStatus status,
                                         @Param("mainCategory") MainCategory mainCategory,
                                         @Param("platformCategory") PlatformCategory platformCategory,
                                         Pageable pageable);

    // 최신순 + 가나다순
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR :mainCategory MEMBER OF p.mainCategory) " +
           "AND (:platformCategory IS NULL OR :platformCategory MEMBER OF p.platformCategory) " +
           "ORDER BY p.createdAt DESC, p.title ASC")
    Page<Post> findRecentPostsByCategory(@Param("status") PostStatus status,
                                        @Param("mainCategory") MainCategory mainCategory,
                                        @Param("platformCategory") PlatformCategory platformCategory,
                                        Pageable pageable);

    // 마감임박순 - PostSchedule과 JOIN + 가나다순
    @Query("SELECT p FROM Post p JOIN p.schedule s WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR :mainCategory MEMBER OF p.mainCategory) " +
           "AND (:platformCategory IS NULL OR :platformCategory MEMBER OF p.platformCategory) " +
           "ORDER BY s.endDate ASC, p.title ASC")
    Page<Post> findDeadlineImminentPostsByCategory(@Param("status") PostStatus status,
                                                   @Param("mainCategory") MainCategory mainCategory,
                                                   @Param("platformCategory") PlatformCategory platformCategory,
                                                   Pageable pageable);

    // 참여자순 + 가나다순
    @Query("SELECT p FROM Post p WHERE p.status = :status " +
           "AND (:mainCategory IS NULL OR :mainCategory MEMBER OF p.mainCategory) " +
           "AND (:platformCategory IS NULL OR :platformCategory MEMBER OF p.platformCategory) " +
           "ORDER BY p.currentParticipants DESC, p.createdAt DESC, p.title ASC")
    Page<Post> findParticipationCountPostsByCategory(@Param("status") PostStatus status,
                                                     @Param("mainCategory") MainCategory mainCategory,
                                                     @Param("platformCategory") PlatformCategory platformCategory,
                                                     Pageable pageable);
}
