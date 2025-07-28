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

    @Column(name = "feedback_method", nullable = false)
    private String feedbackMethod;

    @Column(name = "duration_time", nullable = false)
    private String durationTime;

    @Column(name = "participation_method", nullable = false)
    private String participationMethod;

    @Column(name = "qna", columnDefinition = "TEXT")
    private String qna;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type")
    private RewardType rewardType;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "gender_requirement")
    private String genderRequirement;

    @Column(name = "age_min")
    private Integer ageMin;

    @Column(name = "age_max")
    private Integer ageMax;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "main_category", nullable = false)
    private MainCategory mainCategory;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "platform_category", nullable = false)
    private PlatformCategory platformCategory;

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participation> participations = new ArrayList<>();

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

    @Builder
    public Post(String title, String serviceSummary, String creatorIntroduction, String description,
                String thumbnailUrl, String feedbackMethod, String durationTime, String participationMethod,
                String qna, RewardType rewardType, Integer maxParticipants, String genderRequirement, 
                Integer ageMin, Integer ageMax,
                LocalDateTime startDate, LocalDateTime endDate, 
                MainCategory mainCategory, PlatformCategory platformCategory, Set<GenreCategory> genreCategories) {
        this.title = title;
        this.serviceSummary = serviceSummary;
        this.creatorIntroduction = creatorIntroduction;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.feedbackMethod = feedbackMethod;
        this.durationTime = durationTime;
        this.participationMethod = participationMethod;
        this.qna = qna;
        this.rewardType = rewardType;
        this.maxParticipants = maxParticipants;
        this.genderRequirement = genderRequirement;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mainCategory = mainCategory;
        this.platformCategory = platformCategory;
        this.status = PostStatus.ACTIVE;
        
        // 장르 카테고리 설정
        if (genreCategories != null) {
            this.genreCategories = new HashSet<>(genreCategories);
        } else {
            this.genreCategories = new HashSet<>();
        }
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

    public void updatePost(String title, String serviceSummary, String creatorIntroduction, String description, 
                String thumbnailUrl, String feedbackMethod, String durationTime, String participationMethod, 
                String qna, RewardType rewardType, Integer maxParticipants, String genderRequirement, 
                Integer ageMin, Integer ageMax,
                LocalDateTime startDate, LocalDateTime endDate,
                MainCategory mainCategory, PlatformCategory platformCategory, Set<GenreCategory> genreCategories) {
        this.title = title;
        this.serviceSummary = serviceSummary;
        this.creatorIntroduction = creatorIntroduction;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.feedbackMethod = feedbackMethod;
        this.durationTime = durationTime;
        this.participationMethod = participationMethod;
        this.qna = qna;
        this.rewardType = rewardType;
        this.maxParticipants = maxParticipants;
        this.genderRequirement = genderRequirement;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mainCategory = mainCategory;
        this.platformCategory = platformCategory;
        
        updateGenreCategories(genreCategories);
    }

    public boolean isOwner(Long userId) {
        return this.createdBy.equals(userId);
    }
    
    public boolean canParticipate() {
        return maxParticipants == null || currentParticipants < maxParticipants;
    }
    
    public boolean isActive() {
        return this.status == PostStatus.ACTIVE;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.endDate);
    }

    public List<GenreCategory> getGenreCategories() {
        return new ArrayList<>(genreCategories);
    }

    public void addGenreCategory(GenreCategory genreCategory) {
        this.genreCategories.add(genreCategory);
    }

    public void removeGenreCategory(GenreCategory genreCategory) {
        this.genreCategories.remove(genreCategory);
    }

    public void updateGenreCategories(Set<GenreCategory> genreCategories) {
        this.genreCategories.clear();
        if (genreCategories != null) {
            this.genreCategories.addAll(genreCategories);
        }
    }

    public void updateThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
