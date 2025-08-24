package com.example.nexus.app.mypage.repository;

import com.example.nexus.app.mypage.domain.UserProfile;
import com.example.nexus.app.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
    
    @Query("SELECT up FROM UserProfile up JOIN FETCH up.interests WHERE up.user = :user")
    Optional<UserProfile> findByUserWithInterests(@Param("user") User user);
}
