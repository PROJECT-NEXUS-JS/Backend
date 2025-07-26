package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.Participation;
import com.example.nexus.app.post.domain.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    Optional<Participation> findByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // 게시글의 참여 신청 목록 (상태별, 사용자 정보 포함)
    @Query("SELECT p " +
            "FROM Participation p " +
            "JOIN FETCH p.user " +
            "WHERE p.post.id = :postId AND p.status = :status " +
            "ORDER BY p.appliedAt ASC")
    List<Participation> findByPostIdAndStatusWithUser(@Param("postId") Long postId, @Param("status")ParticipationStatus status);

    // 사용자가 참여한 게시글 목록 (상태별, 게시글 정보 포함)
    @Query("SELECT p " +
            "FROM Participation p " +
            "JOIN FETCH p.post " +
            "WHERE p.user.id = :userId AND p.status = :status " +
            "ORDER BY p.appliedAt DESC")
    List<Participation> findByUserIdAndStatusWithPost(@Param("userId") Long userId, @Param("status") ParticipationStatus status);

    // 게시글 작성자의 참여 관리 목록
    @Query("SELECT p " +
            "FROM Participation p " +
            "JOIN FETCH p.user WHERE p.post.createdBy = :creatorId AND p.status = :status " +
            "ORDER BY p.appliedAt ASC")
    List<Participation> findByPostCreatorIdAndStatusWithUser(@Param("creatorId") Long creatorId, @Param("status") ParticipationStatus status);

    // 게시글별 참여 현황 (상태별)
    List<Participation> findByPostIdAndStatus(Long postId, ParticipationStatus status);

    List<Participation> findByPostId(Long postId);

    // 통계 조회용
    long countByPostIdAndStatus(Long postId, ParticipationStatus status);

    long countByUserIdAndStatus(Long userId, ParticipationStatus status);

    // 특정 기간 참여 신청 조회
    @Query("SELECT p " +
            "FROM Participation p " +
            "WHERE p.appliedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY p.appliedAt DESC")
    List<Participation> findByAppliedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
