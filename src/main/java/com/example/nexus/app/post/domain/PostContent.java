package com.example.nexus.app.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

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

    @Column(name = "participation_method", nullable = false)
    private String participationMethod;

    @Column(name = "story_guide", columnDefinition = "TEXT")
    private String storyGuide;

    @ElementCollection
    @CollectionTable(name = "post_media_urls", joinColumns = @JoinColumn(name = "post_content_id"))
    @Column(name = "media_url")
    private List<String> mediaUrls = new ArrayList<>();

    public static PostContent create(Post post, String participationMethod,
                                     String storyGuide, List<String> mediaUrls) {
        PostContent content = new PostContent();
        content.post = post;
        content.participationMethod = participationMethod;
        content.storyGuide = storyGuide;
        content.mediaUrls = mediaUrls != null ? new ArrayList<>(mediaUrls) : new ArrayList<>();
        return content;
    }

    public void update(String participationMethod, String storyGuide, List<String> mediaUrls) {
        this.participationMethod = participationMethod;
        this.storyGuide = storyGuide;
        this.mediaUrls = mediaUrls != null ? new ArrayList<>(mediaUrls) : new ArrayList<>();
    }
}
