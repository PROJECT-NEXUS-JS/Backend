package com.example.nexus.app.mypage.repository;

import com.example.nexus.app.mypage.domain.UserProfile;
import com.example.nexus.app.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
}
