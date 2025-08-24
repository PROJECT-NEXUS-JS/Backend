package com.example.nexus.app.account.domain;

import com.example.nexus.app.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AccountInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "birth_year")
    private String birthYear;

    @Column(name = "gender")
    private String gender;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "account_preferred_genres", joinColumns = @JoinColumn(name = "account_info_id"))
    @Column(name = "preferred_genre")
    private List<String> preferredGenres = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public AccountInfo(User user, String phoneNumber, String birthYear, String gender, List<String> preferredGenres) {
        this.user = user;
        this.phoneNumber = phoneNumber;
        this.birthYear = birthYear;
        this.gender = gender;
        this.preferredGenres = preferredGenres != null ? preferredGenres : new ArrayList<>();
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public void updateGender(String gender) {
        this.gender = gender;
    }

    public void updatePreferredGenres(List<String> preferredGenres) {
        this.preferredGenres = preferredGenres != null ? preferredGenres : new ArrayList<>();
    }
}
