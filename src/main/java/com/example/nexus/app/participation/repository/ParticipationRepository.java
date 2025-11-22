package com.example.nexus.app.participation.repository;

import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipationRepository extends JpaRepository<Participation, Long>, ParticipationRepositoryCustom {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostIdAndStatus(Long userId, Long postId, ParticipationStatus status);

    // 사용자의 모든 참가 신청 내역
    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.post po " +
            "JOIN FETCH p.user " +
            "LEFT JOIN FETCH po.schedule " +
            "LEFT JOIN FETCH po.requirement " +
            "LEFT JOIN FETCH po.reward " +
            "LEFT JOIN FETCH po.feedback " +
            "LEFT JOIN FETCH po.postContent " +
            "WHERE p.user.id = :userId " +
            "ORDER BY p.appliedAt DESC")
    Page<Participation> findByUserIdWithPost(@Param("userId") Long userId, Pageable pageable);

    // 사용자의 특정 상태 참가 신청 내역
    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.post po " +
            "JOIN FETCH p.user " +
            "LEFT JOIN FETCH po.schedule " +
            "LEFT JOIN FETCH po.requirement " +
            "LEFT JOIN FETCH po.reward " +
            "LEFT JOIN FETCH po.feedback " +
            "LEFT JOIN FETCH po.postContent " +
            "WHERE p.user.id = :userId AND p.status = :status " +
            "ORDER BY p.appliedAt DESC")
    Page<Participation> findByUserIdAndStatusWithPost(@Param("userId") Long userId,
                                                      @Param("status") ParticipationStatus status,
                                                      Pageable pageable);

    // 게시글의 참가 신청자 목록 조회
    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.user " +
            "JOIN FETCH p.post po " +
            "LEFT JOIN FETCH po.schedule " +
            "LEFT JOIN FETCH po.requirement " +
            "LEFT JOIN FETCH po.reward " +
            "LEFT JOIN FETCH po.feedback " +
            "LEFT JOIN FETCH po.postContent " +
            "WHERE p.post.id = :postId " +
            "ORDER BY p.appliedAt ASC")
    Page<Participation> findByPostIdWithUser(@Param("postId") Long postId, Pageable pageable);

    // 게시글의 특정 상태 참가 신청자 목록 조회
    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.user " +
            "JOIN FETCH p.post po " +
            "LEFT JOIN FETCH po.schedule " +
            "LEFT JOIN FETCH po.requirement " +
            "LEFT JOIN FETCH po.reward " +
            "LEFT JOIN FETCH po.feedback " +
            "LEFT JOIN FETCH po.postContent " +
            "WHERE p.post.id = :postId AND p.status = :status " +
            "ORDER BY p.appliedAt ASC")
    Page<Participation> findByPostIdAndStatusWithUser(@Param("postId") Long postId,
                                                      @Param("status") ParticipationStatus status,
                                                      Pageable pageable);

    @Query("SELECT p.post.id " +
            "FROM Participation p " +
            "WHERE p.user.id = :userId AND p.post.id IN :postIds " +
            "AND p.status = 'APPROVED'")
    Set<Long> findApprovedPostIdsByUserIdAndPostIds(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    @Query("SELECT p " + "FROM Participation p " +
            "JOIN FETCH p.user " +
            "WHERE p.post.id = :postId AND p.status = :status " +
            "ORDER BY p.appliedAt DESC")
    Page<Participation> findByPostIdAndStatus(@Param("postId") Long postId, @Param("status") ParticipationStatus status,
                                              Pageable pageable);

    Long countByPostIdAndStatus(Long postId, ParticipationStatus status);

    Long countByPostIdAndStatusAndAppliedAtBefore(Long postId, ParticipationStatus status, LocalDateTime dateTime);

    Long countByPostIdAndStatusAndApprovedAtBefore(Long postId, ParticipationStatus status, LocalDateTime dateTime);

    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.user " +
            "WHERE p.post.id = :postId")
    Page<Participation> findByPostId(Long postId, Pageable pageable);

    @Query("SELECT p FROM Participation p JOIN FETCH p.post WHERE p.user.id = :userId AND p.status = :status")
    List<Participation> findByUserIdAndStatusWithPost(@Param("userId") Long userId,
                                                      @Param("status") ParticipationStatus status);

    long countByUserIdAndStatus(Long userId, ParticipationStatus status);

    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.post po " +
            "LEFT JOIN FETCH po.reward " +
            "WHERE p.id = :participationId")
    Optional<Participation> findByIdWithPostAndReward(@Param("participationId") Long participationId);

    @Query("SELECT DATE(p.appliedAt) as date, COUNT(p) as count " +
            "FROM Participation p " +
            "WHERE p.post.id = :postId AND p.status = :status " +
            "AND p.appliedAt >= :startDate AND p.appliedAt < :endDate " +
            "GROUP BY DATE(p.appliedAt) " +
            "ORDER BY DATE(p.appliedAt)")
    List<Object[]> countByPostIdAndStatusGroupByDate(@Param("postId") Long postId,
                                                     @Param("status") ParticipationStatus status,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.user " +
            "JOIN FETCH p.post " +
            "WHERE p.id = :participationId")
    Optional<Participation> findByIdWithUserAndPost(@Param("participationId") Long participationId);

    // 뱃지 시스템용 카운팅 메서드
    /**
     * 사용자의 승인된 참여 횟수 조회
     */
    @Query("SELECT COUNT(p) FROM Participation p WHERE p.user.id = :userId AND p.status = 'APPROVED'")
    long countApprovedParticipationsByUserId(@Param("userId") Long userId);

    /**
     * 사용자의 완료된 참여 횟수 조회 (리뷰 작성한 참여)
     */
    @Query("SELECT COUNT(DISTINCT p) FROM Participation p " +
           "JOIN Review r ON r.postId = p.post.id AND r.createdBy.id = p.user.id " +
           "WHERE p.user.id = :userId AND p.status = 'APPROVED'")
    long countCompletedParticipationsByUserId(@Param("userId") Long userId);

    /**
     * 기획자의 게시글에 승인된 총 테스터 수 조회
     */
    @Query("SELECT COUNT(p) FROM Participation p " +
           "WHERE p.post.createdBy = :creatorId AND p.status = 'APPROVED'")
    long countApprovedParticipantsByPostCreator(@Param("creatorId") Long creatorId);

    // 결제 상태 관련 메서드
    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.user " +
            "WHERE p.post.id = :postId AND p.status = :status AND p.isPaid = :isPaid " +
            "ORDER BY p.appliedAt ASC")
    Page<Participation> findByPostIdAndStatusAndIsPaidWithUser(
            @Param("postId") Long postId,
            @Param("status") ParticipationStatus status,
            @Param("isPaid") Boolean isPaid,
            Pageable pageable);

    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.user " +
            "WHERE p.user.id = :userId AND p.status = :status AND p.isPaid = :isPaid " +
            "ORDER BY p.appliedAt DESC")
    Page<Participation> findByUserIdAndStatusAndIsPaidWithPost(
            @Param("userId") Long userId,
            @Param("status") ParticipationStatus status,
            @Param("isPaid") Boolean isPaid,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM Participation p "
            + "WHERE p.post.id = :postId AND p.status = :status AND p.isPaid = :isPaid")
    Long countByPostIdAndStatusAndIsPaid(
            @Param("postId") Long postId,
            @Param("status") ParticipationStatus status,
            @Param("isPaid") Boolean isPaid);
}
