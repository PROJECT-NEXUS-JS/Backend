package com.example.nexus.app.user.repository;

import com.example.nexus.app.user.domain.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    @Query("SELECT ui FROM UserInterest ui WHERE ui.user.id = :userId")
    Optional<UserInterest> findByUserId(@Param("userId") Long userId);
}
