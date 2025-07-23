package com.example.nexus.app.global.oauth.service.handler;

import com.example.nexus.app.global.code.BaseErrorCode;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdTokenLoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.error("OIDC 로그인 실패: {}", exception.getMessage());

        BaseErrorCode errorCode;
        if (exception.getCause() instanceof GeneralException) {
            errorCode = ((GeneralException) exception.getCause()).getCode();
        } else {
            errorCode = ErrorStatus.UNAUTHORIZED;
        }

        sendFailureResponse(response, errorCode);
    }

    private void sendFailureResponse(HttpServletResponse response, BaseErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> apiResponse = errorCode.toResponse(null);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
