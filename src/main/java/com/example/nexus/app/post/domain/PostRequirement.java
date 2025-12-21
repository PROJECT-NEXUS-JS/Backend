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
@Table(name = "post_requirements", uniqueConstraints = @UniqueConstraint(columnNames = "post_id"))
@Getter
@NoArgsConstructor
public class PostRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "gender_requirement")
    private String genderRequirement;

    @Column(name = "age_min")
    private Integer ageMin;

    @Column(name = "age_max")
    private Integer ageMax;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_screener_questions", joinColumns = @JoinColumn(name = "post_requirement_id"))
    @Column(name = "screener_question")
    @BatchSize(size = 100)
    private List<String> screenerQuestions = new ArrayList<>();

    @Builder
    public PostRequirement(Post post, Integer maxParticipants, String genderRequirement,
                          Integer ageMin, Integer ageMax, List<String> screenerQuestions) {
        this.post = post;
        this.maxParticipants = maxParticipants;
        this.genderRequirement = genderRequirement;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        if (screenerQuestions != null) {
            this.screenerQuestions = new ArrayList<>(screenerQuestions);
        }
    }

    public void updateMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void updateGenderRequirement(String genderRequirement) {
        this.genderRequirement = genderRequirement;
    }

    public void updateAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    public void updateAgeMax(Integer ageMax) {
        this.ageMax = ageMax;
    }

    public void updateScreenerQuestions(List<String> screenerQuestions) {
        this.screenerQuestions.clear();
        if (screenerQuestions != null) {
            this.screenerQuestions.addAll(screenerQuestions);
        }
    }
}
