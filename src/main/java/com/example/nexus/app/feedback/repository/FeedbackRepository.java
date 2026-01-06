package com.example.nexus.app.feedback.repository;

import com.example.nexus.app.feedback.domain.Feedback;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT f FROM Feedback f JOIN FETCH f.participation WHERE f.participation.id = :participationId")
    Optional<Feedback> findByParticipationId(@Param("participationId") Long participationId);

    @Query("SELECT f FROM Feedback f JOIN FETCH f.participation WHERE f.participation.post.id = :postId ORDER BY f.createdAt DESC")
    List<Feedback> findByPostId(@Param("postId") Long postId);

    @Query("SELECT f FROM Feedback f JOIN FETCH f.participation WHERE f.participation.user.id = :userId ORDER BY f.createdAt DESC")
    List<Feedback> findByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Feedback f JOIN FETCH f.participation p WHERE p.post.id = :postId AND p.user.id = :userId")
    Optional<Feedback> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.participation.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    @Query("SELECT f FROM Feedback f JOIN FETCH f.participation p WHERE p.post.id = :postId " +
           "AND f.createdAt >= :startDate AND f.createdAt < :endDate ORDER BY f.createdAt DESC")
    List<Feedback> findByPostIdAndDateRange(
            @Param("postId") Long postId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

