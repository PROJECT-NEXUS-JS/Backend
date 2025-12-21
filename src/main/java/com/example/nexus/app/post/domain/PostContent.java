package com.example.nexus.app.post.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "post_contents", uniqueConstraints = @UniqueConstraint(columnNames = "post_id"))
@Getter
@NoArgsConstructor
public class PostContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "participation_method")
    private String participationMethod;

    @Column(name = "story_guide", columnDefinition = "TEXT")
    private String storyGuide;

    @ElementCollection
    @CollectionTable(name = "post_media_urls", joinColumns = @JoinColumn(name = "post_content_id"))
    @Column(name = "media_url")
    @BatchSize(size = 100)
    private List<String> mediaUrls = new ArrayList<>();

    @Builder
    public PostContent(Post post, String participationMethod, String storyGuide, List<String> mediaUrls) {
        this.post = post;
        this.participationMethod = participationMethod;
        this.storyGuide = storyGuide;
        this.mediaUrls = mediaUrls != null ? new ArrayList<>(mediaUrls) : new ArrayList<>();
    }

    public void updateParticipationMethod(String participationMethod) {
        this.participationMethod = participationMethod;
    }

    public void updateStoryGuide(String storyGuide) {
        this.storyGuide = storyGuide;
    }

    public void updateMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls != null ? new ArrayList<>(mediaUrls) : new ArrayList<>();
    }

    public List<String> getMediaUrls() {
        return mediaUrls != null ? new ArrayList<>(mediaUrls) : new ArrayList<>();
    }
}
