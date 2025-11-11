package com.example.nexus.app.dashboard.service;

import com.example.nexus.app.dashboard.controller.dto.response.datacenter.DataCenterResponse;
import com.example.nexus.app.dashboard.controller.dto.response.datacenter.ReportDataResponse;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 리포트 생성 서비스
 * - PDF 리포트 데이터 생성
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReportService {

    private final DataCenterService dataCenterService;
    private final PostRepository postRepository;
    private final TemplateEngine templateEngine;

    /**
     * PDF 리포트용 데이터 생성
     *
     * @param postId 프로젝트 ID
     * @param days   조회 기간
     * @return 리포트 데이터
     */
    public ReportDataResponse generateReportData(Long postId, int days) {
        // 프로젝트 정보 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 데이터센터 데이터 조회
        DataCenterResponse dataCenterData = dataCenterService.getDataCenterData(postId, days);

        // 리포트 데이터 생성
        return ReportDataResponse.builder()
                .generatedAt(LocalDateTime.now())
                .postId(postId)
                .postTitle(post.getTitle())
                .periodDays(days)
                .data(dataCenterData)
                .logoUrl(null) // TODO: 실제 로고 URL 설정
                .build();
    }

    /**
     * PDF 파일 생성
     * - Thymeleaf로 HTML 생성 후 OpenHTMLtoPDF로 PDF 변환
     * - 한글 완벽 지원
     * 
     * @param postId 프로젝트 ID
     * @param days   조회 기간
     * @return PDF 바이트 배열
     */
    public byte[] generatePdfReport(Long postId, int days) {
        try {
            ReportDataResponse reportData = generateReportData(postId, days);
            
            // Thymeleaf로 HTML 생성
            String html = generateHtmlFromTemplate(reportData);
            
            // HTML을 PDF로 변환
            return convertHtmlToPdf(html);
        } catch (Exception e) {
            log.error("PDF 생성 중 오류 발생", e);
            throw new RuntimeException("PDF 생성 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Thymeleaf 템플릿으로 HTML 생성
     */
    private String generateHtmlFromTemplate(ReportDataResponse reportData) {
        Context context = new Context();
        
        // 날짜 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        context.setVariable("postTitle", reportData.postTitle());
        context.setVariable("generatedAt", reportData.generatedAt().format(formatter));
        context.setVariable("periodDays", reportData.periodDays());
        context.setVariable("summary", reportData.data().summary());
        context.setVariable("evaluation", reportData.data().overallEvaluation());
        context.setVariable("usability", reportData.data().usabilityEvaluation());
        context.setVariable("keywords", reportData.data().insights().keywords());
        
        return templateEngine.process("pdf/feedback-report", context);
    }

    /**
     * HTML을 PDF로 변환 (Flying Saucer 사용, 한글 폰트 적용)
     */
    private byte[] convertHtmlToPdf(String html) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            ITextRenderer renderer = new ITextRenderer();
            
            // Noto Sans KR 폰트 로드
            try {
                // classpath에서 폰트 파일 찾기
                java.io.InputStream fontStream = getClass().getClassLoader()
                    .getResourceAsStream("static/fonts/NotoSansKR.ttf");
                
                if (fontStream != null) {
                    // 임시 파일로 저장
                    java.io.File tempFont = java.io.File.createTempFile("NotoSansKR", ".ttf");
                    tempFont.deleteOnExit();
                    
                    java.nio.file.Files.copy(fontStream, tempFont.toPath(), 
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    fontStream.close();
                    
                    // Flying Saucer에 폰트 등록
                    renderer.getFontResolver().addFont(
                        tempFont.getAbsolutePath(),
                        true  // embedded
                    );
                    
                    log.info("한글 폰트 로드 완료: {}", tempFont.getAbsolutePath());
                } else {
                    log.warn("NotoSansKR.ttf 폰트 파일을 찾을 수 없습니다.");
                }
            } catch (Exception fontError) {
                log.error("폰트 로드 실패: {}", fontError.getMessage(), fontError);
            }
            
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            
            log.info("PDF 생성 완료");
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("PDF 변환 실패: {}", e.getMessage(), e);
            throw new RuntimeException("PDF 생성 실패: " + e.getMessage(), e);
        } finally {
            outputStream.close();
        }
    }
}

