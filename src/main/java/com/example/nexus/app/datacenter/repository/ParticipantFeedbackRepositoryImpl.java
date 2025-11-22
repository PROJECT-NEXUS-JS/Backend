package com.example.nexus.app.datacenter.repository;

import com.example.nexus.app.datacenter.domain.ParticipantFeedback;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.nexus.app.datacenter.domain.QParticipantFeedback.participantFeedback;

@Repository
@RequiredArgsConstructor
public class ParticipantFeedbackRepositoryImpl implements ParticipantFeedbackRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ParticipantFeedback> findByPostIdAndDateRange(
            Long postId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return queryFactory
                .selectFrom(participantFeedback)
                .where(
                        participantFeedback.postId.eq(postId),
                        participantFeedback.createdAt.goe(startDate),
                        participantFeedback.createdAt.lt(endDate)
                )
                .fetch();
    }

    @Override
    public Long countByPostId(Long postId) {
        Long result = queryFactory
                .select(participantFeedback.count())
                .from(participantFeedback)
                .where(participantFeedback.postId.eq(postId))
                .fetchOne();
        return result != null ? result : 0L;
    }

    @Override
    public Long countByPostIdAndDateRange(
            Long postId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        Long result = queryFactory
                .select(participantFeedback.count())
                .from(participantFeedback)
                .where(
                        participantFeedback.postId.eq(postId),
                        participantFeedback.createdAt.goe(startDate),
                        participantFeedback.createdAt.lt(endDate)
                )
                .fetchOne();
        return result != null ? result : 0L;
    }

    @Override
    public Long countBugReportsByPostId(Long postId) {
        Long result = queryFactory
                .select(participantFeedback.count())
                .from(participantFeedback)
                .where(
                        participantFeedback.postId.eq(postId),
                        participantFeedback.hasBug.eq(true)
                )
                .fetchOne();
        return result != null ? result : 0L;
    }

    @Override
    public Long countPositiveFeedbackByPostId(Long postId) {
        Long result = queryFactory
                .select(participantFeedback.count())
                .from(participantFeedback)
                .where(
                        participantFeedback.postId.eq(postId),
                        participantFeedback.overallSatisfaction.goe(4)
                )
                .fetchOne();
        return result != null ? result : 0L;
    }

    @Override
    public Double getAverageOverallSatisfaction(Long postId) {
        return queryFactory
                .select(participantFeedback.overallSatisfaction.avg())
                .from(participantFeedback)
                .where(participantFeedback.postId.eq(postId))
                .fetchOne();
    }

    @Override
    public Double getAverageRecommendationIntent(Long postId) {
        return queryFactory
                .select(participantFeedback.recommendationIntent.avg())
                .from(participantFeedback)
                .where(participantFeedback.postId.eq(postId))
                .fetchOne();
    }

    @Override
    public Double getAverageReuseIntent(Long postId) {
        return queryFactory
                .select(participantFeedback.reuseIntent.avg())
                .from(participantFeedback)
                .where(participantFeedback.postId.eq(postId))
                .fetchOne();
    }
}

