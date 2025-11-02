package com.example.nexus.app.participation.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "참여자 목록 조회 검색 조건")
public record ParticipantSearchRequest(
        @Schema(description = """
              참여자 상태 필터
              - PENDING: 승인 대기
              - APPROVED: 진행중
              - COMPLETED: 테스트 완료
              - PAID: 지급 완료
              - REJECTED: 거절됨
              - null: 전체
              """,
                example = "COMPLETED")
        String status,

        @Schema(description = "닉네임 또는 이메일 검색", example = "홍길동")
        String searchKeyword,

        @Schema(description = """
              정렬 방향 (신청일시 기준)
              - ASC: 오래된순
              - DESC: 최신순
              """,
                defaultValue = "DESC")
        String sortDirection
) {
    public ParticipantSearchRequest {
        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "DESC";
        }
    }
}
