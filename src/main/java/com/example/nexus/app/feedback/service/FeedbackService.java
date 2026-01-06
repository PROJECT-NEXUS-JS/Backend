package com.example.nexus.app.feedback.service;

import com.example.nexus.app.feedback.controller.dto.request.FeedbackDraftRequest;
import com.example.nexus.app.feedback.controller.dto.request.FeedbackSubmitRequest;
import com.example.nexus.app.feedback.controller.dto.response.FeedbackDraftResponse;
import com.example.nexus.app.feedback.controller.dto.response.FeedbackResponse;
import com.example.nexus.app.feedback.controller.dto.response.MyFeedbackStatusResponse;
import com.example.nexus.app.feedback.controller.dto.response.PresignedUrlResponse;
import com.example.nexus.app.feedback.domain.Feedback;
import com.example.nexus.app.feedback.domain.FeedbackDraft;
import com.example.nexus.app.feedback.repository.FeedbackDraftRepository;
import com.example.nexus.app.feedback.repository.FeedbackRepository;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.repository.ParticipationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackDraftRepository feedbackDraftRepository;
    private final ParticipationRepository participationRepository;
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private static final String FEEDBACK_FOLDER = "feedback/screenshots/";
    private static final long PRESIGNED_URL_EXPIRATION_SECONDS = 300L;

    @Transactional
    public FeedbackDraftResponse saveDraft(FeedbackDraftRequest request, Long userId) {
        log.info("[임시저장] participationId={}, userId={}", request.participationId(), userId);
        
        Participation participation = getParticipation(request.participationId());
        log.info("[임시저장] Participation 조회 성공: id={}, userId={}, postId={}", 
                participation.getId(), participation.getUser().getId(), participation.getPost().getId());

        if (!participation.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.ACCESS_DENIED);
        }

        if (feedbackRepository.findByParticipationId(participation.getId()).isPresent()) {
            throw new GeneralException(ErrorStatus.FEEDBACK_ALREADY_EXISTS);
        }

        FeedbackDraft draft = feedbackDraftRepository.findByParticipationId(participation.getId())
                .orElseGet(() -> FeedbackDraft.builder()
                        .participation(participation)
                        .build());

        draft.update(
                request.overallSatisfaction(),
                request.recommendationIntent(),
                request.reuseIntent(),
                request.mostInconvenient(),
                request.hasBug(),
                request.bugTypes(),
                request.bugLocation(),
                request.bugDescription(),
                request.screenshotUrls(),
                request.functionalityScore(),
                request.comprehensibilityScore(),
                request.speedScore(),
                request.responseTimingScore(),
                request.goodPoints(),
                request.improvementSuggestions(),
                request.additionalComments()
        );

        FeedbackDraft savedDraft = feedbackDraftRepository.save(draft);
        log.info("피드백 임시저장 완료: draftId={}, participationId={}", savedDraft.getId(), participation.getId());

        return FeedbackDraftResponse.from(savedDraft);
    }

    @Transactional
    public FeedbackResponse submitFeedback(FeedbackSubmitRequest request, Long userId) {
        Participation participation = getParticipation(request.participationId());

        if (!participation.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.ACCESS_DENIED);
        }

        // 중복 제출 체크 먼저
        if (feedbackRepository.findByParticipationId(participation.getId()).isPresent()) {
            throw new GeneralException(ErrorStatus.FEEDBACK_ALREADY_EXISTS);
        }

        Feedback feedback = Feedback.builder()
                .participation(participation)
                .overallSatisfaction(request.overallSatisfaction())
                .recommendationIntent(request.recommendationIntent())
                .reuseIntent(request.reuseIntent())
                .mostInconvenient(request.mostInconvenient())
                .hasBug(request.hasBug())
                .bugTypes(request.bugTypes())
                .bugLocation(request.bugLocation())
                .bugDescription(request.bugDescription())
                .screenshotUrls(request.screenshotUrls())
                .functionalityScore(request.functionalityScore())
                .comprehensibilityScore(request.comprehensibilityScore())
                .speedScore(request.speedScore())
                .responseTimingScore(request.responseTimingScore())
                .goodPoints(request.goodPoints())
                .improvementSuggestions(request.improvementSuggestions())
                .additionalComments(request.additionalComments())
                .build();

        Feedback savedFeedback = feedbackRepository.save(feedback);

        feedbackDraftRepository.findByParticipationId(participation.getId())
                .ifPresent(feedbackDraftRepository::delete);

        if (participation.isApproved()) {
            participation.completeTest();
            participationRepository.save(participation);
            log.info("피드백 제출로 인한 참여 완료 처리: participationId={}, status={}", 
                    participation.getId(), participation.getStatus());
        }

        log.info("피드백 제출 완료: feedbackId={}, participationId={}, userId={}", 
                savedFeedback.getId(), participation.getId(), userId);

        return FeedbackResponse.from(savedFeedback);
    }

    public PresignedUrlResponse generatePresignedUrl(String fileName, String contentType) {
        String uniqueFileName = FEEDBACK_FOLDER + UUID.randomUUID() + "_" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(PRESIGNED_URL_EXPIRATION_SECONDS))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        URL uploadUrl = presignedRequest.url();

        String fileUrl = s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucketName).key(uniqueFileName))
                .toExternalForm();

        log.info("Presigned URL 생성 완료: fileName={}, uniqueFileName={}", fileName, uniqueFileName);

        return new PresignedUrlResponse(
                uploadUrl.toString(),
                fileUrl,
                PRESIGNED_URL_EXPIRATION_SECONDS
        );
    }

    @Transactional(readOnly = true)
    public MyFeedbackStatusResponse getMyFeedbackStatus(Long postId, Long userId) {
        log.info("[상태조회] postId={}, userId={}", postId, userId);
        
        // participationId 조회
        Participation participation = participationRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPATION_NOT_FOUND_FEEDBACK));
        Long participationId = participation.getId();
        log.info("[상태조회] participationId={} 조회 완료", participationId);
        
        var feedback = feedbackRepository.findByPostIdAndUserId(postId, userId);
        if (feedback.isPresent()) {
            log.info("[상태조회] 제출된 피드백 존재: feedbackId={}", feedback.get().getId());
            Feedback feedbackEntity = feedback.get();
            feedbackEntity.getBugTypes().size();
            feedbackEntity.getScreenshotUrls().size();
            return MyFeedbackStatusResponse.submitted(participationId, FeedbackResponse.from(feedbackEntity));
        }

        var draft = feedbackDraftRepository.findByPostIdAndUserId(postId, userId);
        if (draft.isPresent()) {
            log.info("[상태조회] 임시저장 존재: draftId={}", draft.get().getId());
            FeedbackDraft draftEntity = draft.get();
            // ElementCollection Lazy loading 강제 초기화
            draftEntity.getBugTypes().size();
            draftEntity.getScreenshotUrls().size();
            return MyFeedbackStatusResponse.draft(participationId, FeedbackDraftResponse.from(draftEntity));
        }

        log.info("[상태조회] 피드백 없음, participationId={}", participationId);
        return MyFeedbackStatusResponse.notStarted(participationId);
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getFeedback(Long feedbackId, Long userId) {
        Feedback feedback = entityManager.createQuery(
                "SELECT f FROM Feedback f JOIN FETCH f.participation p JOIN FETCH p.user JOIN FETCH p.post WHERE f.id = :feedbackId",
                Feedback.class)
                .setParameter("feedbackId", feedbackId)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.FEEDBACK_NOT_FOUND));

        if (!feedback.getParticipation().getUser().getId().equals(userId) &&
                !feedback.getParticipation().getPost().getCreatedBy().equals(userId)) {
            throw new GeneralException(ErrorStatus.ACCESS_DENIED);
        }

        // ElementCollection Lazy loading 강제 초기화
        feedback.getBugTypes().size();
        feedback.getScreenshotUrls().size();

        return FeedbackResponse.from(feedback);
    }

    private Participation getParticipation(Long participationId) {
        log.info("[getParticipation] participationId={} 조회 시작", participationId);
        
        // Native Query로 직접 조회
        Participation participation = entityManager.find(Participation.class, participationId);
        
        if (participation == null) {
            log.error("[getParticipation] Participation not found: participationId={}", participationId);
            throw new GeneralException(ErrorStatus.PARTICIPATION_NOT_FOUND_FEEDBACK);
        }
        
        log.info("[getParticipation] 조회 성공: id={}, userId={}, postId={}", 
                participation.getId(), participation.getUser().getId(), participation.getPost().getId());
        return participation;
    }
}

