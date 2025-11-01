package com.example.nexus.app.participation.repository;

import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import com.example.nexus.app.reward.domain.QParticipantReward;
import com.example.nexus.app.reward.domain.RewardStatus;
import com.example.nexus.app.user.domain.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.nexus.app.participation.domain.QParticipation.participation;
import static com.example.nexus.app.reward.domain.QParticipantReward.participantReward;
import static com.example.nexus.app.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class ParticipationRepositoryImpl implements ParticipationRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Participation> findParticipantsWithFilters(Long postId, String status, String rewardStatus, String nickname,
                                                           String sortBy, String sortDirection, Pageable pageable) {

        JPAQuery<Participation> query = queryFactory
                .selectFrom(participation)
                .leftJoin(participation.user, user)
                .fetchJoin()
                .leftJoin(participantReward).on(participantReward.participation.id.eq(participation.id))
                .where(
                        participation.post.id.eq(postId),
                        statusCondition(status),
                        rewardStatusCondition(rewardStatus, participantReward),
                        nicknameCondition(nickname, user)
                );

        // 정렬 조건
        List<Participation> participations = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(participation.count())
                .from(participation)
                .leftJoin(participantReward).on(participantReward.participation.id.eq(participation.id))
                .where(
                        participation.post.id.eq(postId),
                        statusCondition(status),
                        rewardStatusCondition(rewardStatus, participantReward),
                        nicknameCondition(nickname, participation.user)
                )
                .fetchOne();

        return new PageImpl<>(participations, pageable, total);
    }

    private BooleanExpression statusCondition(String status) {
        return StringUtils.hasText(status) ? participation.status.eq(ParticipationStatus.valueOf(status)) : null;
    }

    private BooleanExpression rewardStatusCondition(String rewardStatus, QParticipantReward participantReward) {
        return StringUtils.hasText(rewardStatus) ? participantReward.rewardStatus.eq(RewardStatus.valueOf(rewardStatus)) : null;
    }

    private BooleanExpression nicknameCondition(String nickname, QUser user) {
        return StringUtils.hasText(nickname) ? user.nickname.containsIgnoreCase(nickname) : null;
    }
}
