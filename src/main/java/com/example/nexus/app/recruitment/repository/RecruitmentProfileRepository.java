package com.example.nexus.app.recruitment.repository;

import com.example.nexus.app.recruitment.domain.RecruitmentProfile;
import com.example.nexus.app.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruitmentProfileRepository extends JpaRepository<RecruitmentProfile, Long> {

    Optional<RecruitmentProfile> findByUser(User user);
}
