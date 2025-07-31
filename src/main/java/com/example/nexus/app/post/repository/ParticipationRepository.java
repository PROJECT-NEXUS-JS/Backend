package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.Participation;
import com.example.nexus.app.post.domain.ParticipationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // 사용자의 모든 참가 신청 내역
    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.post " +
            "WHERE p.user.id = :userId " +
            "ORDER BY p.appliedAt DESC")
    Page<Participation> findByUserIdWithPost(@Param("userId") Long userId, Pageable pageable);

    // 사용자의 특정 상태 참가 신청 내역
    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.post " +
            "WHERE p.user.id = :userId AND p.status = :status " +
            "ORDER BY p.appliedAt DESC")
    Page<Participation> findByUserIdAndStatusWithPost(@Param("userId") Long userId, @Param("status") ParticipationStatus status,
                                                      Pageable pageable);

    // 게시글의 참가 신청자 목록 조회
    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.user " +
            "WHERE p.post.id = :postId " +
            "ORDER BY p.appliedAt ASC")
    Page<Participation> findByPostIdWithUser(@Param("postId") Long postId, Pageable pageable);

    // 게시글의 특정 상태 참가 신청자 목록 조회
    @Query("SELECT p FROM Participation p " +
            "JOIN FETCH p.user " +
            "WHERE p.post.id = :postId AND p.status = :status " +
            "ORDER BY p.appliedAt ASC")
    Page<Participation> findByPostIdAndStatusWithUser(@Param("postId") Long postId, @Param("status") ParticipationStatus status,
                                                      Pageable pageable);
}
