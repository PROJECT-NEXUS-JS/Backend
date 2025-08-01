package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.PostSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostScheduleRepository extends JpaRepository<PostSchedule, Long> {

    Optional<PostSchedule> findByPostId(Long postId);

    @Query("SELECT ps " +
            "FROM PostSchedule ps " +
            "WHERE ps.recruitmentDeadline <= :now AND ps.recruitmentDeadline > :yesterday")
    List<PostSchedule> findRecruitmentDeadlineToday(@Param("now") LocalDateTime now, @Param("yesterday") LocalDateTime yesterday);

    @Query("SELECT ps " +
            "FROM PostSchedule ps " +
            "WHERE ps.endDate <= :now AND ps.endDate > :yesterday")
    List<PostSchedule> findEndDateToday(@Param("now") LocalDateTime now, @Param("yesterday") LocalDateTime yesterday);
}
