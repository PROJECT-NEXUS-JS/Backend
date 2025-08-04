package com.example.nexus.app.recruitment.domain;

import com.example.nexus.app.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "recruitment_profiles")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RecruitmentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "introduction", length = 300)
    private String introduction;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static RecruitmentProfile create(User user, String introduction) {
        RecruitmentProfile profile = new RecruitmentProfile();
        profile.user = user;
        profile.introduction = introduction;
        return profile;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
