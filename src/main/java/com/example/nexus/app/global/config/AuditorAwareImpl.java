package com.example.nexus.app.global.config;

import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        log.info("=== JPA Auditing Debug ===");
        log.info("Authentication: {}", authentication);

        if (authentication != null) {
            log.info("Principal: {}", authentication.getPrincipal());
            log.info("Principal Type: {}", authentication.getPrincipal().getClass());
            log.info("Name: {}", authentication.getName());
            log.info("IsAuthenticated: {}", authentication.isAuthenticated());
            log.info("Authorities: {}", authentication.getAuthorities());
        } else {
            log.warn("Authentication is null");
        }

        log.info("========================");

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("인증되지 않은 사용자 - Optional.empty() 반환");
            return Optional.empty();
        }

        // CustomUserDetails에서 직접 userId 가져오기
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            Long userId = userDetails.getUserId();
            log.info("CustomUserDetails에서 가져온 userId: {}", userId);
            return Optional.of(userId);
        }

        log.warn("Principal이 CustomUserDetails가 아님: {}", principal.getClass());
        return Optional.empty();
    }
}
