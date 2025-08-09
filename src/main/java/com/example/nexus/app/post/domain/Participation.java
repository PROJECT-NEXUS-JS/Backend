package com.example.nexus.app.post.domain;

import com.example.nexus.app.post.domain.dto.ParticipationApplicationDto;
import com.example.nexus.app.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "participations", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status;

    @Column(name = "applicant_name", nullable = false, length = 10)
    private String applicantName;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(name = "applicant_email")
    private String applicantEmail;

    @Column(name = "application_reason", nullable = false, columnDefinition = "TEXT")
    private String applicationReason;

    @Column(name = "privacy_agreement", nullable = false)
    private Boolean privacyAgreement;

    @Column(name = "terms_agreement", nullable = false)
    private Boolean termsAgreement;

    @Builder
    public Participation(Post post, User user, ParticipationStatus status, String applicantName,
                         String contactNumber, String applicantEmail, String applicationReason, Boolean privacyAgreement,
                         Boolean termsAgreement) {
        this.post = post;
        this.user = user;
        this.status = status;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.contactNumber = contactNumber;
        this.applicationReason = applicationReason;
        this.privacyAgreement = privacyAgreement;
        this.termsAgreement = termsAgreement;
    }

    public static Participation createApplication(Post post, User user, ParticipationApplicationDto applicationDto) {

        return Participation.builder()
                .post(post)
                .user(user)
                .status(ParticipationStatus.PENDING)
                .applicantName(applicationDto.applicantName())
                .applicantEmail(applicationDto.applicantEmail())
                .contactNumber(applicationDto.contactNumber())
                .applicationReason(applicationDto.applicationReason())
                .privacyAgreement(applicationDto.privacyAgreement())
                .termsAgreement(applicationDto.termsAgreement())
                .build();
    }

    public void approve() {
        this.status = ParticipationStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = ParticipationStatus.REJECTED;
        this.approvedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.status == ParticipationStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == ParticipationStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.status == ParticipationStatus.REJECTED;
    }
}
