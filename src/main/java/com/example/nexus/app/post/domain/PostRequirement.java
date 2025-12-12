package com.example.nexus.app.post.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "additional_requirements", columnDefinition = "TEXT")
    private String additionalRequirements;

    @Builder
    public PostRequirement(Post post, Integer maxParticipants, String genderRequirement,
                          Integer ageMin, Integer ageMax, String additionalRequirements) {
        this.post = post;
        this.maxParticipants = maxParticipants;
        this.genderRequirement = genderRequirement;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.additionalRequirements = additionalRequirements;
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

    public void updateAdditionalRequirements(String additionalRequirements) {
        this.additionalRequirements = additionalRequirements;
    }
}
