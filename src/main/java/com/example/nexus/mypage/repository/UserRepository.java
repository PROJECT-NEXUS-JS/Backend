package com.example.nexus.mypage.repository;

import com.example.nexus.mypage.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}