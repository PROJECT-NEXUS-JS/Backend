package com.example.nexus.app.post.domain;

import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "posts")
@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "service_summary", nullable = false)
    private String serviceSummary;

    @Column(name = "creator_introduction", nullable = false)
    private String creatorIntroduction;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "post_main_category", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "main_category", nullable = false)
    private Set<MainCategory> mainCategory;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "post_platform_category", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "platform_category", nullable = false)
    private Set<PlatformCategory> platformCategory;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "post_genres", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "genre")
    private Set<GenreCategory> genreCategories = new HashSet<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants = 0;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PostSchedule schedule;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PostRequirement requirement;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PostReward reward;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PostFeedback feedback;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PostContent postContent;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    public Post(String title, String serviceSummary, String creatorIntroduction, String description,
                String thumbnailUrl, Set<MainCategory> mainCategory, Set<PlatformCategory> platformCategory,
                Set<GenreCategory> genreCategories, PostStatus status) {
        this.title = title;
        this.serviceSummary = serviceSummary;
        this.creatorIntroduction = creatorIntroduction;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.status = status;

        if (mainCategory != null) {
            this.mainCategory = new HashSet<>(mainCategory);
        }
        if (platformCategory != null) {
            this.platformCategory = new HashSet<>(platformCategory);
        }
        if (genreCategories != null) {
            this.genreCategories = new HashSet<>(genreCategories);
        }
    }

    public Post(String title, String serviceSummary, String creatorIntroduction, String description,
                String thumbnailUrl, Set<MainCategory> mainCategory, Set<PlatformCategory> platformCategory,
                Set<GenreCategory> genreCategories) {
        this(title, serviceSummary, creatorIntroduction, description, thumbnailUrl, 
             mainCategory, platformCategory, genreCategories, PostStatus.DRAFT);
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementParticipants() {
        this.currentParticipants++;
    }

    public void decrementParticipants() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    public void active() {
        this.status = PostStatus.ACTIVE;
    }

    public void draft() {
        this.status = PostStatus.DRAFT;
    }

    public boolean isOwner(Long userId) {
        return this.createdBy.equals(userId);
    }

    public boolean canParticipate() {
        if (requirement == null) {
            return true;
        }
        Integer maxParticipants = requirement.getMaxParticipants();
        return maxParticipants == null || currentParticipants < maxParticipants;
    }

    public boolean isActive() {
        return this.status == PostStatus.ACTIVE;
    }

    public boolean isDraft() {
        return this.status == PostStatus.DRAFT;
    }

    public boolean isExpired() {
        return schedule != null &&
                LocalDateTime.now().isAfter(schedule.getEndDate());
    }

    public List<GenreCategory> getGenreCategories() {
        return new ArrayList<>(genreCategories);
    }

    public void updateMainCategories(Set<MainCategory> mainCategory) {
        this.mainCategory.clear();
        if (mainCategory != null) {
            this.mainCategory.addAll(mainCategory);
        }
    }

    public void updatePlatformCategories(Set<PlatformCategory> platformCategory) {
        this.platformCategory.clear();
        if (platformCategory != null) {
            this.platformCategory.addAll(platformCategory);
        }
    }

    public void updateGenreCategories(Set<GenreCategory> genreCategories) {
        this.genreCategories.clear();
        if (genreCategories != null) {
            this.genreCategories.addAll(genreCategories);
        }
    }

    public void updateBasicInfo(String title, String serviceSummary,
                                String creatorIntroduction, String description,
                                String thumbnailUrl) {
        this.title = title;
        this.serviceSummary = serviceSummary;
        this.creatorIntroduction = creatorIntroduction;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }
}
