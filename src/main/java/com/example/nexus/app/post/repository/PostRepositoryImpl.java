package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.controller.dto.PostSearchCondition;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostSortType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.nexus.app.post.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> findPostWithCondition(PostSearchCondition condition, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // 기본 조건: ACTIVE
        builder.and(post.status.eq(condition.getStatus()));

        // 모집 마감일이 현재 시간 이후인 게시글만 조회
        LocalDateTime now = LocalDateTime.now(KOREA_ZONE_ID);
        builder.and(post.schedule.recruitmentDeadline.goe(now));

        // 메인 카테고리 조건
        if (condition.getMainCategory() != null) {
            builder.and(post.mainCategory.contains(condition.getMainCategory()));
        }

        // 플랫폼 카테고리 조건
        if (condition.getPlatformCategory() != null) {
            builder.and(post.platformCategory.contains(condition.getPlatformCategory()));
        }

        // 장르 카테고리 조건
        if (condition.getGenreCategory() != null) {
            builder.and(post.genreCategories.contains(condition.getGenreCategory()));
        }

        // 키워드 검색 조건
        if (condition.getKeyword() != null && !condition.getKeyword().trim().isEmpty()) {
            String keyword = "%" + condition.getKeyword().trim() + "%";
            builder.and(
                    post.title.like(keyword)
                            .or(post.description.like(keyword))
            );
        }

        // 정렬 조건
        OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(condition.getSortBy());

        // 데이터 조회
        List<Post> content = queryFactory
                .selectFrom(post)
                .leftJoin(post.schedule).fetchJoin()
                .leftJoin(post.requirement).fetchJoin()
                .leftJoin(post.reward).fetchJoin()
                .leftJoin(post.feedback).fetchJoin()
                .leftJoin(post.postContent).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(String sortBy) {
        PostSortType sortType = PostSortType.fromCode(sortBy);

        return switch (sortType) {
            case POPULAR -> new OrderSpecifier[] {
                    post.likeCount.desc(),
                    post.createdAt.desc()
            };
            case DEADLINE -> new OrderSpecifier[]{
                    post.schedule.endDate.asc(),
                    post.createdAt.desc()
            };
            case VIEW_COUNT -> new OrderSpecifier[]{
                    post.viewCount.desc(),
                    post.createdAt.desc()
            };
            case LATEST -> new OrderSpecifier[]{
                    post.createdAt.desc()
            };
        };
    }
}
