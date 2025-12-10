package com.example.nexus.app.post.domain;

import jakarta.persistence.*;
import lombok.Builder;
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

    private String privacyPurpose;

    @Builder
    public PostFeedback(Post post, String feedbackMethod, List<String> feedbackItems,
                       Set<PrivacyItem> privacyItems, String privacyPurpose) {
        this.post = post;
        this.feedbackMethod = feedbackMethod;
        if (feedbackItems != null) {
            this.feedbackItems.addAll(feedbackItems);
        }
        if (privacyItems != null) {
            this.privacyItems.addAll(privacyItems);
        }
        this.privacyPurpose = privacyPurpose;
    }

    public void updateFeedbackMethod(String feedbackMethod) {
        this.feedbackMethod = feedbackMethod;
    }

    public void updateFeedbackItems(List<String> feedbackItems) {
        this.feedbackItems.clear();
        if (feedbackItems != null) {
            this.feedbackItems.addAll(feedbackItems);
        }
    }

    public void updatePrivacyItems(Set<PrivacyItem> privacyItems) {
        this.privacyItems.clear();
        if (privacyItems != null) {
            this.privacyItems.addAll(privacyItems);
        }
    }

    public void updatePrivacyPurpose(String privacyPurpose) {
        this.privacyPurpose = privacyPurpose;
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
