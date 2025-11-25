package com.example.nexus.app.reward.repository;

import com.example.nexus.app.reward.domain.ParticipantReward;
import com.example.nexus.app.reward.domain.RewardStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipantRewardRepository extends JpaRepository<ParticipantReward, Long> {

    Optional<ParticipantReward> findByParticipationId(Long participationId);

    @Query("SELECT COUNT(pr) " +
            "FROM ParticipantReward pr " +
            "WHERE pr.participation.post.id =:postId AND pr.rewardStatus = :status")
    Long countByPostIdAndRewardStatus(@Param("postId") Long postId, @Param("status") RewardStatus status);

    @Query("SELECT pr FROM ParticipantReward pr " +
            "WHERE pr.participation.id IN :participationIds")
    List<ParticipantReward> findByParticipationIds(@Param("participationIds") List<Long> participationIds);

    @Query("SELECT COUNT(pr) " +
            "FROM ParticipantReward pr " +
            "WHERE pr.participation.post.id = :postId " +
            "AND pr.rewardStatus = :status " +
            "AND pr.createdAt < :beforeDate")
    Long countByPostIdAndRewardStatusAndCreatedAtBefore(
            @Param("postId") Long postId,
            @Param("status") RewardStatus status,
            @Param("beforeDate") LocalDateTime beforeDate
    );
}
