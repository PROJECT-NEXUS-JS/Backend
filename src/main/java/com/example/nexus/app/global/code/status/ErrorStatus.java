package com.example.nexus.app.global.code.status;

import com.example.nexus.app.global.code.BaseErrorCode;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 400 Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALID4001", "입력값 유효성 검증에 실패했습니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "VALID4002", "필수 파라미터가 누락되었습니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "VALID4003", "파라미터 타입이 올바르지 않습니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "COMMON415", "지원하지 않는 미디어 타입입니다."),
    UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "COMMON422", "처리할 수 없는 엔티티입니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH4011", "인증이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH4012", "인증 정보가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4013", "토큰이 유효하지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4014", "토큰이 만료되었습니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH4031", "권한이 없는 리소스입니다."),
    ACCOUNT_BANNED(HttpStatus.FORBIDDEN, "AUTH4032", "사용이 제한된 계정입니다."),

    // 404 Not Found
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4041", "사용자를 찾을 수 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "GEN4042", "리소스를 찾을 수 없습니다."),
    ENDPOINT_NOT_FOUND(HttpStatus.NOT_FOUND, "GEN4043", "존재하지 않는 엔드포인트입니다."),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "허용되지 않는 HTTP 메서드입니다."),

    // 409 Conflict
    CONFLICT(HttpStatus.CONFLICT, "COMMON409", "요청이 현재 리소스 상태와 충돌합니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "GEN4091", "이미 존재하는 리소스입니다."),
    VERSION_CONFLICT(HttpStatus.CONFLICT, "GEN4092", "리소스 버전 충돌이 발생했습니다."),

    // 429 Too Many Requests
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "COMMON429", "요청이 너무 많습니다. 잠시 후 다시 시도하세요."),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "GEN4291", "요청 한도를 초과했습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB5001", "데이터베이스 처리 중 오류가 발생했습니다."),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "COMMON502", "불완전한 게이트웨이 응답을 받았습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COMMON503", "서비스를 일시적으로 사용할 수 없습니다."),
    GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "COMMON504", "게이트웨이 연결이 시간 초과되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public boolean isSuccess() { return false; }
}
