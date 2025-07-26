package com.example.nexus.app.global.config;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
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

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("인증되지 않은 사용자 - Optional.empty() 반환");
//            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        // CustomUserDetails에서 직접 userId 가져오기
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            Long userId = userDetails.getUserId();
            return Optional.of(userId);
        }

        return Optional.empty();
    }
}
