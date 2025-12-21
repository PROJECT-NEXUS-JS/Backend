package com.example.nexus.app.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_schedules", uniqueConstraints = @UniqueConstraint(columnNames = "post_id"))
@Getter
@NoArgsConstructor
public class PostSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "recruitment_deadline")
    private LocalDateTime recruitmentDeadline;

    @Column(name = "duration_time")
    private String durationTime;

    @Builder
    public PostSchedule(Post post, LocalDateTime startDate, LocalDateTime endDate,
                        LocalDateTime recruitmentDeadline, String durationTime) {
        this.post = post;
        this.startDate = startDate;
        this.endDate = endDate;
        this.recruitmentDeadline = recruitmentDeadline;
        this.durationTime = durationTime;
    }

    public void updateStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void updateEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void updateRecruitmentDeadline(LocalDateTime recruitmentDeadline) {
        this.recruitmentDeadline = recruitmentDeadline;
    }

    public void updateDurationTime(String durationTime) {
        this.durationTime = durationTime;
    }
}
