package com.example.nexus.app.global.security;

import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.oauth.service.IdTokenService;
import com.example.nexus.app.global.oauth.service.handler.IdTokenLoginFailureHandler;
import com.example.nexus.app.global.oauth.service.handler.IdTokenLoginSuccessHandler;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final IdTokenLoginSuccessHandler idTokenLoginSuccessHandler;
    private final IdTokenLoginFailureHandler idTokenLoginFailureHandler;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final IdTokenService idTokenService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**", "/api-test/**"
                        ).permitAll()
                        .requestMatchers("/signup","/auth/me").authenticated()
                        .anyRequest().permitAll()
                );

        // id_token 로그인 필터
        http.addFilterBefore(requestHeaderAuthenticationFilter(), BasicAuthenticationFilter.class);
        // JWT 인증 재발급 필터
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), RequestHeaderAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() {
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();

        filter.setRequiresAuthenticationRequestMatcher(request ->
                "POST".equalsIgnoreCase(request.getMethod()) &&
                        "/auth/login".equals(request.getServletPath())
        );

        filter.setPrincipalRequestHeader("id_token");
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(idTokenLoginSuccessHandler);
        filter.setAuthenticationFailureHandler(idTokenLoginFailureHandler);

        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            String token = (String) authentication.getPrincipal();
            CustomUserDetails user = idTokenService.loadUserByAccessToken(token);
            return new PreAuthenticatedAuthenticationToken(
                    user,
                    token,
                    user.getAuthorities()
            );
        };
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationProcessingFilter(jwtService, userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
