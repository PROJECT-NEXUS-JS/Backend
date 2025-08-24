package com.example.nexus.app.account.repository;

import com.example.nexus.app.account.domain.AccountInfo;
import com.example.nexus.app.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountInfoRepository extends JpaRepository<AccountInfo, Long> {
    Optional<AccountInfo> findByUser(User user);
}
