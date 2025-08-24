package com.example.nexus.app.account.repository;

import com.example.nexus.app.account.domain.AccountInfo;
import com.example.nexus.app.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountInfoRepository extends JpaRepository<AccountInfo, Long> {
    Optional<AccountInfo> findByUser(User user);
    
    @Query("SELECT ai FROM AccountInfo ai JOIN FETCH ai.preferredGenres WHERE ai.user = :user")
    Optional<AccountInfo> findByUserWithPreferredGenres(@Param("user") User user);
}
