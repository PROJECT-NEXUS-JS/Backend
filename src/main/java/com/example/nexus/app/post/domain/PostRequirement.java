package com.example.nexus.app.post.domain;

import jakarta.persistence.*;
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

    public static PostRequirement create(Post post, Integer maxParticipants,
                                         String genderRequirement, Integer ageMin, Integer ageMax,
                                         String additionalRequirements) {
        PostRequirement requirement = new PostRequirement();
        requirement.post = post;
        requirement.maxParticipants = maxParticipants;
        requirement.genderRequirement = genderRequirement;
        requirement.ageMin = ageMin;
        requirement.ageMax = ageMax;
        requirement.additionalRequirements = additionalRequirements;
        return requirement;
    }

    public void update(Integer maxParticipants, String genderRequirement, 
                      Integer ageMin, Integer ageMax, String additionalRequirements) {
        this.maxParticipants = maxParticipants;
        this.genderRequirement = genderRequirement;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.additionalRequirements = additionalRequirements;
    }
}
