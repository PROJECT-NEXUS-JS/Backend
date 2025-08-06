package com.example.nexus.app.user.domain;

import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_interests")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_interest_main_categories", joinColumns = @JoinColumn(name = "user_interest_id"))
    @Column(name = "main_category")
    private Set<MainCategory> mainCategories = new HashSet<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_interest_platform_categories", joinColumns = @JoinColumn(name = "user_interest_id"))
    @Column(name = "platform_category")
    private Set<PlatformCategory> platformCategories = new HashSet<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_interest_genre_categories", joinColumns = @JoinColumn(name = "user_interest_id"))
    @Column(name = "genre_category")
    private Set<GenreCategory> genreCategories = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public UserInterest(User user, Set<MainCategory> mainCategories, 
                       Set<PlatformCategory> platformCategories, 
                       Set<GenreCategory> genreCategories) {
        this.user = user;
        if (mainCategories != null) {
            this.mainCategories = new HashSet<>(mainCategories);
        }
        if (platformCategories != null) {
            this.platformCategories = new HashSet<>(platformCategories);
        }
        if (genreCategories != null) {
            this.genreCategories = new HashSet<>(genreCategories);
        }
    }

    public void updateInterests(Set<MainCategory> mainCategories, 
                               Set<PlatformCategory> platformCategories, 
                               Set<GenreCategory> genreCategories) {
        this.mainCategories.clear();
        this.platformCategories.clear();
        this.genreCategories.clear();
        
        if (mainCategories != null) {
            this.mainCategories.addAll(mainCategories);
        }
        if (platformCategories != null) {
            this.platformCategories.addAll(platformCategories);
        }
        if (genreCategories != null) {
            this.genreCategories.addAll(genreCategories);
        }
    }
} 