package com.example.nexus.app.participation.repository;

import static com.example.nexus.app.participation.domain.QParticipation.participation;
import static com.example.nexus.app.reward.domain.QParticipantReward.participantReward;
import static com.example.nexus.app.user.domain.QUser.user;

import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ParticipationRepositoryImpl implements ParticipationRepositoryCustom {
    private static final String PAID_STATUS = "PAID";

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Participation> findParticipantsWithFilters(Long postId, String status,
                                                           String searchKeyword, Pageable pageable) {

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(pageable);

        List<Participation> participations = queryFactory
                .selectFrom(participation)
                .leftJoin(participation.user, user).fetchJoin()
                .leftJoin(participation.participantReward, participantReward).fetchJoin()
                .where(
                        participation.post.id.eq(postId),
                        statusCondition(status),
                        searchKeywordCondition(searchKeyword)
                )
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(participation.count())
                .from(participation)
                .where(
                        participation.post.id.eq(postId),
                        statusCondition(status),
                        searchKeywordCondition(searchKeyword)
                )
                .fetchOne();

        long total = getTotalCount(totalCount);

        return new PageImpl<>(participations, pageable, total);
    }

    private OrderSpecifier<?> getOrderSpecifier(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return createOrderSpecifierFromPageable(pageable);
        }
        return participation.appliedAt.desc();
    }

    private OrderSpecifier<?> createOrderSpecifierFromPageable(Pageable pageable) {
        Sort.Order order = pageable.getSort().iterator().next();

        if (order.isAscending()) {
            return participation.appliedAt.asc();
        }
        return participation.appliedAt.desc();
    }

    private long getTotalCount(Long totalCount) {
        if (totalCount == null) {
            return 0L;
        }
        return totalCount;
    }

    private BooleanExpression statusCondition(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }

        if (PAID_STATUS.equalsIgnoreCase(status)) {
            return participation.status.eq(ParticipationStatus.COMPLETED)
                    .and(participation.isPaid.isTrue());
        }

        return participation.status.eq(ParticipationStatus.valueOf(status));
    }

    private BooleanExpression searchKeywordCondition(String searchKeyword) {
        if (!StringUtils.hasText(searchKeyword)) {
            return null;
        }
        return participation.user.nickname.containsIgnoreCase(searchKeyword)
                .or(participation.applicantEmail.containsIgnoreCase(searchKeyword));
    }
}
