package com.example.nexus.app.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "recruitment_deadline")
    private LocalDateTime recruitmentDeadline;

    @Column(name = "duration_time", nullable = false)
    private String durationTime;

    public static PostSchedule create(Post post, LocalDateTime startDate, LocalDateTime endDate,
                                      LocalDateTime recruitmentDeadline, String durationTime) {
        PostSchedule schedule = new PostSchedule();
        schedule.post = post;
        schedule.startDate = startDate;
        schedule.endDate = endDate;
        schedule.recruitmentDeadline = recruitmentDeadline;
        schedule.durationTime = durationTime;
        return schedule;
    }

    public void update(LocalDateTime startDate, LocalDateTime endDate,
                      LocalDateTime recruitmentDeadline, String durationTime) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.recruitmentDeadline = recruitmentDeadline;
        this.durationTime = durationTime;
    }
}
