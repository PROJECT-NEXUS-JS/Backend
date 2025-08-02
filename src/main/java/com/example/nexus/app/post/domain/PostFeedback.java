package com.example.nexus.app.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_feedbacks", uniqueConstraints = @UniqueConstraint(columnNames = "post_id"))
@Getter
@NoArgsConstructor
public class PostFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "feedback_method", nullable = false)
    private String feedbackMethod;

    @Column(name = "feedback_items", columnDefinition = "TEXT")
    private String feedbackItems;

    @Column(name = "privacy_collection_items", columnDefinition = "TEXT")
    private String privacyCollectionItems;

    public static PostFeedback create(Post post, String feedbackMethod,
                                      String feedbackItems, String privacyCollectionItems) {
        PostFeedback feedback = new PostFeedback();
        feedback.post = post;
        feedback.feedbackMethod = feedbackMethod;
        feedback.feedbackItems = feedbackItems;
        feedback.privacyCollectionItems = privacyCollectionItems;
        return feedback;
    }

    public void update(String feedbackMethod, String feedbackItems, String privacyCollectionItems) {
        this.feedbackMethod = feedbackMethod;
        this.feedbackItems = feedbackItems;
        this.privacyCollectionItems = privacyCollectionItems;
    }
}
