package com.example.nexus.app.dashboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_view_logs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "view_date"}))
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PostViewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "view_date", nullable = false)
    private LocalDate viewDate;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static PostViewLog create(Long postId, LocalDate date, Long count) {
        PostViewLog log = new PostViewLog();
        log.postId = postId;
        log.viewDate = date;
        log.viewCount = count;
        return log;
    }

    public void updateViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
}
