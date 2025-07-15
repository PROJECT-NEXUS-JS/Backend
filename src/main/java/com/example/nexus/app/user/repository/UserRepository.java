package com.example.nexus.app.user.repository;

import com.example.nexus.app.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 조회 (소셜 로그인 시 기존 유저 판별용)
    @Transactional(readOnly = true)
    Optional<User> findByEmail(String email);
    // 리프레시 토큰 검증용
    @Transactional(readOnly = true)
    Optional<User> findByRefreshToken(String refreshToken);
    Boolean existsByNickname(String nickname);
    Optional<User> findByNickname(String nickname);
}
