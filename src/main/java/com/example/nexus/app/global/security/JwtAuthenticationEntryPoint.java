package com.example.nexus.app.global.security;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ApiResponse<Object> UNAUTHORIZED_RESPONSE =
            ApiResponse.onFailure(ErrorStatus.UNAUTHORIZED.getCode(), ErrorStatus.UNAUTHORIZED.getMessage(), null);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        try (OutputStream os = response.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, UNAUTHORIZED_RESPONSE);
            os.flush();
        }
    }
}
