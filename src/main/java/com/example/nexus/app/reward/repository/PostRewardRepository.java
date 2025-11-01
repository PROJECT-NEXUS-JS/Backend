package com.example.nexus.app.reward.repository;

import com.example.nexus.app.reward.domain.PostReward;
import com.example.nexus.app.reward.domain.RewardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRewardRepository extends JpaRepository<PostReward, Long> {

    Optional<PostReward> findByPostId(Long postId);

    List<PostReward> findByRewardType(RewardType rewardType);

    @Query("SELECT pr " +
            "FROM PostReward pr " +
            "WHERE pr.rewardType = :rewardType AND pr.rewardDescription IS NOT NULL")
    List<PostReward> findByRewardTypeWithDescription(@Param("rewardType") RewardType rewardType);
}
