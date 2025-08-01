package com.example.nexus.app.global.security;

import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtService.extractRefreshToken(request).isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {

        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);
        if (accessTokenOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = accessTokenOpt.get();
        if (jwtService.isTokenValid(accessToken) && !tokenBlacklistService.isTokenBlacklisted(accessToken)) {
            jwtService.extractEmail(accessToken)
                    .flatMap(userRepository::findByEmail)
                    .ifPresent(this::saveAuthentication);
        }

        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(User myUser) {
        CustomUserDetails userDetailsUser = new CustomUserDetails(
                Collections.singleton(new SimpleGrantedAuthority(myUser.getRoleType().toString())),
                myUser.getEmail(),
                myUser.getRoleType(),
                myUser.getId());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
