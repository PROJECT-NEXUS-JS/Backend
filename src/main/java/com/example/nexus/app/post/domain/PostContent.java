package com.example.nexus.app.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "media_url")
    private String mediaUrl;

    public static PostContent create(Post post, String participationMethod,
                                     String storyGuide, String mediaUrl) {
        PostContent content = new PostContent();
        content.post = post;
        content.participationMethod = participationMethod;
        content.storyGuide = storyGuide;
        content.mediaUrl = mediaUrl;
        return content;
    }

    public void update(String participationMethod, String storyGuide, String mediaUrl) {
        this.participationMethod = participationMethod;
        this.storyGuide = storyGuide;
        this.mediaUrl = mediaUrl;
    }
}
