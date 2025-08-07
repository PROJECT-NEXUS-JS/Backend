package com.example.nexus.app.dashboard.repository;

import com.example.nexus.app.dashboard.domain.DailyPostStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyPostStatisticsRepository extends JpaRepository<DailyPostStatistics, Long> {
}
