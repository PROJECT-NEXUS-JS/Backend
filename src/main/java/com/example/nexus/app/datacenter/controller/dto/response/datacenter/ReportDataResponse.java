package com.example.nexus.app.datacenter.controller.dto.response.datacenter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * PDF 리포트 생성용 데이터 응답 DTO
 * - 프론트엔드에서 PDF로 변환하기 위한 전체 데이터 제공
 */
@Builder
@Schema(description = "PDF 리포트 데이터")
public record ReportDataResponse(
        @Schema(description = "리포트 생성 일시")
        LocalDateTime generatedAt,

        @Schema(description = "프로젝트 ID")
        Long postId,

        @Schema(description = "프로젝트 제목")
        String postTitle,

        @Schema(description = "조회 기간 (일)")
        int periodDays,

        @Schema(description = "데이터센터 전체 데이터")
        DataCenterResponse data,

        @Schema(description = "로고 URL (옵션)")
        String logoUrl
) {
}

