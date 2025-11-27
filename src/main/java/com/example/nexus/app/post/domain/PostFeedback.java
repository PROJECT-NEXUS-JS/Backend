package com.example.nexus.app.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "feedback_method")
    private String feedbackMethod;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_feedback_items", joinColumns = @JoinColumn(name = "post_feedback_id"))
    @Column(name = "feedback_item")
    private List<String> feedbackItems = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "post_privacy_item", joinColumns = @JoinColumn(name = "post_feedback_id"))
    @Column(name = "privacy_item")
    private Set<PrivacyItem> privacyItems = new HashSet<>();

    public static PostFeedback create(Post post, String feedbackMethod,
                                      List<String> feedbackItems, Set<PrivacyItem> privacyItems) {
        PostFeedback feedback = new PostFeedback();
        feedback.post = post;
        feedback.feedbackMethod = feedbackMethod;
        if (feedbackItems != null) {
            feedback.feedbackItems.addAll(feedbackItems);
        }
        if (privacyItems != null) {
            feedback.privacyItems.addAll(privacyItems);
        }
        return feedback;
    }

    public void update(String feedbackMethod, List<String> feedbackItems, Set<PrivacyItem> privacyItems) {
        this.feedbackMethod = feedbackMethod;
        this.feedbackItems.clear();
        if (feedbackItems != null) {
            this.feedbackItems.addAll(feedbackItems);
        }
        this.privacyItems.clear();
        if (privacyItems != null) {
            this.privacyItems.addAll(privacyItems);
        }
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

    public Set<PrivacyItem> getPrivacyItems() {
        return privacyItems;
    }
}
