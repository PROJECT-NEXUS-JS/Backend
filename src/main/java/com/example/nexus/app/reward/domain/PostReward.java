package com.example.nexus.app.reward.domain;

import com.example.nexus.app.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
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
    @JsonIgnore
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type")
    private RewardType rewardType;

    @Column(name = "reward_description", columnDefinition = "TEXT")
    private String rewardDescription;

    @Builder
    public PostReward(Post post, RewardType rewardType, String rewardDescription) {
        this.post = post;
        this.rewardType = rewardType;
        this.rewardDescription = rewardDescription;
    }

    public void updateRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public void updateRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }
}
