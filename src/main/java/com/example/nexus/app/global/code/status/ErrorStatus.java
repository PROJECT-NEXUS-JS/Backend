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
    INVALID_MAIN_CATEGORY(HttpStatus.BAD_REQUEST, "CATEGORY4004", "유효하지 않은 메인 카테고리입니다."),
    INVALID_PLATFORM_CATEGORY(HttpStatus.BAD_REQUEST, "CATEGORY4005", "유효하지 않은 플랫폼 카테고리입니다."),
    FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "FILE4001", "업로드할 파일이 없습니다."),
    POST_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "POST4006", "활성화되지 않은 게시글입니다."),
    POST_EXPIRED(HttpStatus.BAD_REQUEST, "POST4007", "마감된 게시글입니다."),
    PARTICIPATION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "PARTICIPANT4008", "참가 인원이 마감되었습니다."),
    CANNOT_CANCEL_APPLICATION(HttpStatus.BAD_REQUEST, "APPLICATION4009", "취소할 수 없는 신청입니다."),
    INTEREST_LENGTH_EXCEEDED(HttpStatus.BAD_REQUEST, "PROFILE400", "각 관심사는 15자 이내로 입력해주세요."),
    POST_SCHEDULE_REQUIRED(HttpStatus.BAD_REQUEST, "POST40010", "게시글 발행을 위해 일정 정보가 필요합니다."),
    POST_REQUIREMENT_REQUIRED(HttpStatus.BAD_REQUEST, "POST400011", "게시글 발행을 위해 참여 조건이 필요합니다."),
    POST_FEEDBACK_REQUIRED(HttpStatus.BAD_REQUEST, "POST40012", "게시글 발행을 위해 피드백 설정이 필요합니다."),
    POST_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "POST400013", "게시글 발행을 위해 콘텐츠 정보가 필요합니다."),
    INVALID_GENRE_CATEGORY(HttpStatus.BAD_REQUEST, "CATEGORY40014", "유효하지 않은 장르 카테고리입니다."),
    POST_NOT_DRAFT(HttpStatus.BAD_REQUEST, "POST40015", "임시저장 상태가 아닌 게시글입니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH4011", "인증이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH4012", "인증 정보가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4013", "토큰이 유효하지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4014", "토큰이 만료되었습니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH4031", "권한이 없는 리소스입니다."),
    ACCOUNT_BANNED(HttpStatus.FORBIDDEN, "AUTH4032", "사용이 제한된 계정입니다."),
    POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "POST4033", "게시글 수정/삭제 권한이 없습니다."),
    APPLICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "APPLICATION4034", "신청서에 대한 접근 권한이 없습니다."),

    // 404 Not Found
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4041", "사용자를 찾을 수 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "GEN4042", "리소스를 찾을 수 없습니다."),
    ENDPOINT_NOT_FOUND(HttpStatus.NOT_FOUND, "GEN4043", "존재하지 않는 엔드포인트입니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION4044", "해당 알림을 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4045", "게시글을 찾을 수 없습니다."),
    GENRE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY4046", "존재하지 않는 장르 카테고리가 포함되어 있습니다."),
    PARTICIPATION_NOT_FOUND(HttpStatus.NOT_FOUND, "PARTICIPATION4047", "참가 신청 여부를 찾을 수 없습니다."),
    POST_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_SCHEDULE4048", "게시글 일정 정보를 찾을 수 없습니다."),
    POST_REQUIREMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_REQUIREMENT4049", "게시글 참여 조건을 찾을 수 없습니다."),


    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "허용되지 않는 HTTP 메서드입니다."),
    POST_REWARD_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_REWARD4051", "게시글 리워드 정보를 찾을 수 없습니다."),
    POST_FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_FEEDBACK4052", "게시글 피드백 설정을 찾을 수 없습니다."),
    POST_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_CONTENT4053", "게시글 콘텐츠 정보를 찾을 수 없습니다."),

    // 409 Conflict
    CONFLICT(HttpStatus.CONFLICT, "COMMON409", "요청이 현재 리소스 상태와 충돌합니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "GEN4091", "이미 존재하는 리소스입니다."),
    VERSION_CONFLICT(HttpStatus.CONFLICT, "GEN4092", "리소스 버전 충돌이 발생했습니다."),
    ALREADY_APPLIED(HttpStatus.CONFLICT, "PARTICIPANT4093", "이미 신청한 게시글입니다."),
    ALREADY_PROCESSED_APPLICATION(HttpStatus.CONFLICT, "APPLICATION4094", "이미 처리된 신청입니다."),
    // 429 Too Many Requests
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "COMMON429", "요청이 너무 많습니다. 잠시 후 다시 시도하세요."),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "GEN4291", "요청 한도를 초과했습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S35001", "파일 업로드 중 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB5001", "데이터베이스 처리 중 오류가 발생했습니다."),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "COMMON502", "불완전한 게이트웨이 응답을 받았습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COMMON503", "서비스를 일시적으로 사용할 수 없습니다."),
    GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "COMMON504", "게이트웨이 연결이 시간 초과되었습니다."),
    S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S35005", "파일 삭제 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public boolean isSuccess() { return false; }
}
