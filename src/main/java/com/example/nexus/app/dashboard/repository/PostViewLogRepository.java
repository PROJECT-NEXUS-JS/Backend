package com.example.nexus.app.dashboard.repository;

import com.example.nexus.app.dashboard.domain.PostViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PostViewLogRepository extends JpaRepository<PostViewLog, Long> {

    Optional<PostViewLog> findByPostIdAndViewDate(Long postId, LocalDate viewDate);

    boolean existsByPostIdAndViewDate(Long postId, LocalDate viewDate);

    @Query("SELECT pvl " +
            "FROM PostViewLog pvl " +
            "WHERE pvl.postId = :postId AND pvl.viewDate " +
            "BETWEEN :startDate AND :endDate ORDER BY pvl.viewDate")
    List<PostViewLog> findByPostIdAndViewDateBetween(@Param("postId") Long postId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
