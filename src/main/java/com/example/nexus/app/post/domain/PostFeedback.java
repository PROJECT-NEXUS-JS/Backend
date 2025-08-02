package com.example.nexus.app.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_feedback_items", joinColumns = @JoinColumn(name = "post_feedback_id"))
    @Column(name = "feedback_item")
    private List<String> feedbackItems;

    @Column(name = "privacy_collection_items", columnDefinition = "TEXT")
    private String privacyCollectionItems;

    public static PostFeedback create(Post post, String feedbackMethod,
                                      List<String> feedbackItems, String privacyCollectionItems) {
        PostFeedback feedback = new PostFeedback();
        feedback.post = post;
        feedback.feedbackMethod = feedbackMethod;
        feedback.feedbackItems = feedbackItems != null ? new ArrayList<>(feedbackItems) : new ArrayList<>();
        feedback.privacyCollectionItems = privacyCollectionItems;
        return feedback;
    }

    public void update(String feedbackMethod, List<String> feedbackItems, String privacyCollectionItems) {
        this.feedbackMethod = feedbackMethod;
        this.feedbackItems.clear();
        if (feedbackItems != null) {
            this.feedbackItems.addAll(feedbackItems);
        }
        this.privacyCollectionItems = privacyCollectionItems;
    }

    public void addFeedbackItem(String item) {
        if (!this.feedbackItems.contains(item)) {
            this.feedbackItems.add(item);
        }
    }

    public void removeFeedbackItem(String item) {
        this.feedbackItems.remove(item);
    }

    public boolean hasFeedbackItem(String item) {
        return this.feedbackItems.contains(item);
    }
}
