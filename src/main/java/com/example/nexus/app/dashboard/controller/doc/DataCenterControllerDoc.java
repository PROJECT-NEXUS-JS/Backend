package com.example.nexus.app.dashboard.controller.doc;

import com.example.nexus.app.dashboard.controller.dto.response.datacenter.*;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "데이터센터", description = "피드백 데이터 집계 및 분석 API")
public interface DataCenterControllerDoc {

    @Operation(
            summary = "데이터센터 전체 데이터 조회",
            description = "참여자 피드백 데이터를 집계하여 통합 대시보드 데이터를 반환합니다. " +
                    "요약, 전반평가, 품질피드백, 사용성평가, 인사이트를 모두 포함합니다."
    )
    ResponseEntity<ApiResponse<DataCenterResponse>> getDataCenter(
            @Parameter(description = "프로젝트 ID") @PathVariable Long postId,
            @Parameter(description = "조회 기간 (일 단위, 기본값: 7일)") @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "요약 정보 조회",
            description = "총 참여자 수, 평균 만족도, 버그 발생률, 긍정 피드백 비율 등 주요 수치를 반환합니다."
    )
    ResponseEntity<ApiResponse<DataCenterSummaryResponse>> getSummary(
            @Parameter(description = "프로젝트 ID") @PathVariable Long postId,
            @Parameter(description = "조회 기간 (일 단위, 기본값: 7일)") @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "전반 평가 조회",
            description = "평균 만족도, 추천 의향, 재이용 의향 점수 및 분포를 반환합니다."
    )
    ResponseEntity<ApiResponse<OverallEvaluationResponse>> getOverallEvaluation(
            @Parameter(description = "프로젝트 ID") @PathVariable Long postId,
            @Parameter(description = "조회 기간 (일 단위, 기본값: 7일)") @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "품질 피드백 조회",
            description = "불편 요소, 버그 비율, 만족도 분포, 문제 유형, 문제 발생 위치 등을 반환합니다."
    )
    ResponseEntity<ApiResponse<QualityFeedbackResponse>> getQualityFeedback(
            @Parameter(description = "프로젝트 ID") @PathVariable Long postId,
            @Parameter(description = "조회 기간 (일 단위, 기본값: 7일)") @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "기능별 사용성 평가 조회",
            description = "기능 작동성, 이해도, 로딩 속도, 반응 타이밍, 안정성 점수를 반환합니다. (Radar 차트용)"
    )
    ResponseEntity<ApiResponse<UsabilityEvaluationResponse>> getUsabilityEvaluation(
            @Parameter(description = "프로젝트 ID") @PathVariable Long postId,
            @Parameter(description = "조회 기간 (일 단위, 기본값: 7일)") @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "개선 제안 및 인사이트 조회",
            description = "좋았던 점, 개선 제안 피드백 리스트와 주요 키워드를 반환합니다."
    )
    ResponseEntity<ApiResponse<InsightsResponse>> getInsights(
            @Parameter(description = "프로젝트 ID") @PathVariable Long postId,
            @Parameter(description = "조회 기간 (일 단위, 기본값: 7일)") @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "PDF 리포트 데이터 조회",
            description = "PDF 리포트 생성을 위한 전체 데이터를 반환합니다. " +
                    "프론트엔드에서 이 데이터를 받아 html2pdf, jsPDF 등을 사용하여 PDF로 변환할 수 있습니다. " +
                    "리포트에는 프로젝트 정보, 생성일시, 로고, 데이터센터 전체 데이터가 포함됩니다."
    )
    ResponseEntity<ApiResponse<ReportDataResponse>> getReportData(
            @Parameter(description = "프로젝트 ID") @PathVariable Long postId,
            @Parameter(description = "조회 기간 (일 단위, 기본값: 7일)") @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "PDF 리포트 다운로드",
            description = "백엔드에서 생성된 PDF 리포트 파일을 다운로드합니다. " +
                    "OpenPDF 라이브러리를 사용하여 서버에서 직접 PDF를 생성합니다. " +
                    "리포트에는 요약 정보, 전반 평가, 사용성 평가, 주요 키워드가 포함됩니다."
    )
    ResponseEntity<byte[]> downloadPdfReport(
            @Parameter(description = "프로젝트 ID") @PathVariable Long postId,
            @Parameter(description = "조회 기간 (일 단위, 기본값: 7일)") @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}

