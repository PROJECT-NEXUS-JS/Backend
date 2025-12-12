package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.schedule " +
            "LEFT JOIN FETCH p.requirement " +
            "LEFT JOIN FETCH p.reward " +
            "LEFT JOIN FETCH p.feedback " +
            "LEFT JOIN FETCH p.postContent " +
            "WHERE p.id = :postId")
    Optional<Post> findByIdWithAllDetails(@Param("postId") Long postId);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.schedule " +
            "LEFT JOIN FETCH p.requirement " +
            "LEFT JOIN FETCH p.reward " +
            "LEFT JOIN FETCH p.feedback " +
            "LEFT JOIN FETCH p.postContent " +
            "WHERE p.status = :status AND p.createdBy = :userId " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findByStatusAndCreatedBy(@Param("status") PostStatus status, @Param("userId") Long userId,
                                        Pageable pageable);

    Page<Post> findByCreatedByAndStatus(Long createdBy, PostStatus status, Pageable pageable);

    long countByCreatedBy(Long userId);

    Optional<Post> findFirstByCreatedByAndStatusOrderByCreatedAtDesc(Long userId, PostStatus status);

    List<Post> findByStatus(PostStatus postStatus);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.schedule " +
            "LEFT JOIN FETCH p.requirement " +
            "LEFT JOIN FETCH p.reward " +
            "LEFT JOIN FETCH p.feedback " +
            "LEFT JOIN FETCH p.postContent " +
            "WHERE p.id = :postId")
    Optional<Post> findByIdWithAllDetailsAndUser(@Param("postId") Long postId);

    // 뱃지 시스템용 카운팅 메서드

    /**
     * 사용자가 작성한 활성 게시글 수 조회
     */
    @Query("SELECT COUNT(p) FROM Post p WHERE p.createdBy = :userId AND p.status = 'ACTIVE'")
    long countActivePostsByUserId(@Param("userId") Long userId);

    @Query(value =
            "SELECT " +
                    "COALESCE(COUNT(DISTINCT pl.id), 0) as totalLikes, " +
                    "COALESCE(COUNT(DISTINCT CASE WHEN pl.created_at < :yesterday THEN pl.id END), 0) as yesterdayLikes, "
                    +
                    "COALESCE(COUNT(DISTINCT CASE WHEN p.status = 'PENDING' THEN p.id END), 0) as totalPendingApplications, "
                    +
                    "COALESCE(COUNT(DISTINCT CASE WHEN p.status = 'PENDING' AND p.applied_at < :yesterday THEN p.id END), 0) as yesterdayPendingApplications, "
                    +
                    "COALESCE(COUNT(DISTINCT CASE WHEN p.status = 'APPROVED' THEN p.id END), 0) as totalApprovedParticipants, "
                    +
                    "COALESCE(COUNT(DISTINCT CASE WHEN p.status = 'APPROVED' AND p.approved_at < :yesterday THEN p.id END), 0) as yesterdayApprovedParticipants, "
                    +
                    "COALESCE(COUNT(DISTINCT r.id), 0) as totalReviews, " +
                    "COALESCE(COUNT(DISTINCT CASE WHEN r.created_at < :yesterday THEN r.id END), 0) as yesterdayReviews, "
                    +
                    "COALESCE(COUNT(DISTINCT CASE WHEN pr.reward_status = 'PENDING' THEN pr.id END), 0) as totalPendingRewards, "
                    +
                    "COALESCE(COUNT(DISTINCT CASE WHEN pr.reward_status = 'PENDING' AND pr.created_at < :yesterday THEN pr.id END), 0) as yesterdayPendingRewards "
                    +
                    "FROM posts po " +
                    "LEFT JOIN post_likes pl ON po.id = pl.post_id " +
                    "LEFT JOIN participations p ON po.id = p.post_id " +
                    "LEFT JOIN reviews r ON po.id = r.post_id " +
                    "LEFT JOIN participant_rewards pr ON p.id = pr.participation_id " +
                    "WHERE po.id = :postId",
            nativeQuery = true)
    List<Object[]> getDashboardStatsByPostId(@Param("postId") Long postId, @Param("yesterday") LocalDateTime yesterday);

    @Query(value =
            "SELECT " +
                    "COALESCE(COUNT(DISTINCT pl.id), 0) as totalLikes, " +
                    "COALESCE(COUNT(DISTINCT CASE WHEN p.status = 'PENDING' THEN p.id END), 0) as totalPendingApplications, " +
                    "COALESCE(COUNT(DISTINCT CASE WHEN p.status = 'APPROVED' THEN p.id END), 0) as totalApprovedParticipants, " +
                    "COALESCE(COUNT(DISTINCT r.id), 0) as totalReviews " +
                    "FROM posts po " +
                    "LEFT JOIN post_likes pl ON po.id = pl.post_id " +
                    "LEFT JOIN participations p ON po.id = p.post_id " +
                    "LEFT JOIN reviews r ON po.id = r.post_id " +
                    "WHERE po.id = :postId",
            nativeQuery = true)
    List<Object[]> getBarChartStatsByPostId(@Param("postId") Long postId);

    /**
     * 조회수 증가 (벌크 업데이트)
     */
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    /**
     * 좋아요 수 증가 (벌크 업데이트)
     */
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId);

    /**
     * 좋아요 수 감소 (벌크 업데이트)
     */
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId AND p.likeCount > 0")
    void decrementLikeCount(@Param("postId") Long postId);
}
