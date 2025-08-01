package com.example.nexus.app.mypage.repository;

import com.example.nexus.app.mypage.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
