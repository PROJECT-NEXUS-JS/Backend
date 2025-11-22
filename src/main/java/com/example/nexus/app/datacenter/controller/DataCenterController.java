package com.example.nexus.app.datacenter.controller;

import com.example.nexus.app.datacenter.controller.doc.DataCenterControllerDoc;
import com.example.nexus.app.datacenter.controller.dto.response.datacenter.*;
import com.example.nexus.app.datacenter.service.DataCenterService;
import com.example.nexus.app.datacenter.service.ReportService;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 데이터센터 API 컨트롤러
 * - 참여자 피드백 데이터를 집계·분석하여 대시보드 제공
 */
@RestController
@RequestMapping("/v1/data-center")
@RequiredArgsConstructor
public class DataCenterController implements DataCenterControllerDoc {

    private final DataCenterService dataCenterService;
    private final ReportService reportService;

    @Override
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<DataCenterResponse>> getDataCenter(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DataCenterResponse response = dataCenterService.getDataCenterData(postId, days);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{postId}/summary")
    public ResponseEntity<ApiResponse<DataCenterSummaryResponse>> getSummary(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DataCenterResponse fullData = dataCenterService.getDataCenterData(postId, days);
        return ResponseEntity.ok(ApiResponse.onSuccess(fullData.summary()));
    }

    @Override
    @GetMapping("/{postId}/evaluation")
    public ResponseEntity<ApiResponse<OverallEvaluationResponse>> getOverallEvaluation(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DataCenterResponse fullData = dataCenterService.getDataCenterData(postId, days);
        return ResponseEntity.ok(ApiResponse.onSuccess(fullData.overallEvaluation()));
    }

    @Override
    @GetMapping("/{postId}/quality")
    public ResponseEntity<ApiResponse<QualityFeedbackResponse>> getQualityFeedback(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DataCenterResponse fullData = dataCenterService.getDataCenterData(postId, days);
        return ResponseEntity.ok(ApiResponse.onSuccess(fullData.qualityFeedback()));
    }

    @Override
    @GetMapping("/{postId}/usability")
    public ResponseEntity<ApiResponse<UsabilityEvaluationResponse>> getUsabilityEvaluation(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DataCenterResponse fullData = dataCenterService.getDataCenterData(postId, days);
        return ResponseEntity.ok(ApiResponse.onSuccess(fullData.usabilityEvaluation()));
    }

    @Override
    @GetMapping("/{postId}/insights")
    public ResponseEntity<ApiResponse<InsightsResponse>> getInsights(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DataCenterResponse fullData = dataCenterService.getDataCenterData(postId, days);
        return ResponseEntity.ok(ApiResponse.onSuccess(fullData.insights()));
    }

    @Override
    @GetMapping("/{postId}/report/data")
    public ResponseEntity<ApiResponse<ReportDataResponse>> getReportData(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ReportDataResponse response = reportService.generateReportData(postId, days);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{postId}/report/pdf")
    public ResponseEntity<byte[]> downloadPdfReport(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        byte[] pdfBytes = reportService.generatePdfReport(postId, days);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            org.springframework.http.ContentDisposition.builder("attachment")
                .filename("feedback-report-" + postId + ".pdf")
                .build()
        );
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(pdfBytes);
    }
}

