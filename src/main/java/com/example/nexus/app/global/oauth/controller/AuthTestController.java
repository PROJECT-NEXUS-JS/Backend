package com.example.nexus.app.global.oauth.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.code.status.SuccessStatus;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.security.JwtService;
import com.example.nexus.app.user.domain.RoleType;
import com.example.nexus.app.user.domain.SocialType;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "User TEST Login", description = "TEST Login API (실제 환경서는 삭제예정)")
public class AuthTestController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/test-login")
    @Operation(summary = "로그인 이후 테스트용 코드", description = "소셜 로그인 API BYPASS 목적의 임시 토큰발급")
    @Parameter(name = "email", description = "Bearer 토큰 발급을 위한 임시 이메일주소", required = true, in = ParameterIn.QUERY)
    public ResponseEntity<AuthTestControllerDto> testLogin(@RequestParam String email) {

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .nickname("테스트유저")
                    .profileUrl(null)
                    .roleType(RoleType.ROLE_GUEST)
                    .socialType(SocialType.APPLE)
                    .oauthId("TEST-" + UUID.randomUUID())
                    .build();
            return userRepository.save(newUser);
        });

        String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtService.createRefreshToken();

        user.updateRefreshToken(refreshToken);
        user.markLogin();
        userRepository.save(user);

        return ResponseEntity.ok(new AuthTestControllerDto(accessToken, refreshToken));
    }

    @GetMapping("/test-login/me")
    @Operation(
            summary = "현재 로그인한 사용자 정보 조회",
            description = "Bearer 토큰을 통해 인증된 사용자의 이메일을 리턴",
            security = @SecurityRequirement(name = "Authorization")
    )
    public ResponseEntity<ApiResponse<CurrentUserResponse>> whoAmI(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (user == null) {
            // 인증 정보가 없으면 401 바인딩
            return ResponseEntity
                    .status(ErrorStatus.UNAUTHORIZED.getHttpStatus())
                    .body(ApiResponse.of(ErrorStatus.UNAUTHORIZED, null));
        }

        CurrentUserResponse dto = new CurrentUserResponse(user.getUserId(), user.getUsername());
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus.OK, dto));
    }

    @Getter @AllArgsConstructor
    static class CurrentUserResponse {
        private Long   userId;
        private String email;
    }
}
