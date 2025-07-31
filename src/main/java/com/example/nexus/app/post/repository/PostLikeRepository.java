package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    // 사용자가 찜한 게시글 목록
    @Query("SELECT pl " +
            "from PostLike pl " +
            "JOIN FETCH pl.post " +
            "WHERE pl.user.id = :userId " +
            "ORDER BY pl.createdAt DESC")
    List<PostLike> findByUserIdWithPost(@Param("userId") Long userId);

    @Query("SELECT pl " +
            "FROM PostLike pl " +
            "JOIN FETCH pl.post " +
            "WHERE pl.user.id = :userId " +
            "ORDER BY pl.createdAt DESC")
    Page<PostLike> findByUserIdWithPostPaged(@Param("userId") Long userId, Pageable pageable);

    // 통계 조회용
    long countByPostId(Long postId);

    long countByUserId(Long userId);

    @Query("SELECT pl.post.id " +
    "FROM PostLike pl " +
    "WHERE pl.user.id = :userId AND pl.post.id IN :postIds")
    Set<Long> findLikedPostIdsByUserIdAndPostIds(@Param("userId") Long userId,
                                                 @Param("postIds") List<Long> postIds);
}
