package com.example.nexus.app.mypage.domain;

import com.example.nexus.app.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "job")
    private String job;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_profile_interests", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Column(name = "interest")
    private List<String> interests = new ArrayList<>();

    @Builder
    public UserProfile(User user, String job, List<String> interests) {
        this.user = user;
        this.job = job;
        this.interests = interests;
    }

    /**
     * 프로필 정보를 수정하는 메소드
     * @param job 수정할 직업
     * @param interests 수정할 관심사 목록
     */
    public void update(String job, List<String> interests) {
        this.job = job;
        this.interests = interests;
    }
}
