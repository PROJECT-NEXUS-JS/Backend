package com.example.nexus.app.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_rewards", uniqueConstraints = @UniqueConstraint(columnNames = "post_id"))
@Getter
@NoArgsConstructor
public class PostReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type")
    private RewardType rewardType;

    @Column(name = "reward_description", columnDefinition = "TEXT")
    private String rewardDescription;

    public static PostReward create(Post post, RewardType rewardType, String rewardDescription) {
        PostReward reward = new PostReward();
        reward.post = post;
        reward.rewardType = rewardType;
        reward.rewardDescription = rewardDescription;
        return reward;
    }

    public void update(RewardType rewardType, String rewardDescription) {
        this.rewardType = rewardType;
        this.rewardDescription = rewardDescription;
    }
}
