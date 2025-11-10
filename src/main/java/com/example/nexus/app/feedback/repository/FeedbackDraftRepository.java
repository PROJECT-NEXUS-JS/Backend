package com.example.nexus.app.feedback.repository;

import com.example.nexus.app.feedback.domain.FeedbackDraft;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackDraftRepository extends JpaRepository<FeedbackDraft, Long> {

    @Query("SELECT fd FROM FeedbackDraft fd JOIN FETCH fd.participation WHERE fd.participation.id = :participationId")
    Optional<FeedbackDraft> findByParticipationId(@Param("participationId") Long participationId);

    @Query("SELECT fd FROM FeedbackDraft fd JOIN FETCH fd.participation p WHERE p.post.id = :postId AND p.user.id = :userId")
    Optional<FeedbackDraft> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(fd) > 0 THEN true ELSE false END FROM FeedbackDraft fd WHERE fd.participation.id = :participationId")
    boolean existsByParticipationId(@Param("participationId") Long participationId);
}

