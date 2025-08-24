package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom{

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.schedule " +
            "LEFT JOIN FETCH p.requirement " +
            "LEFT JOIN FETCH p.reward " +
            "LEFT JOIN FETCH p.feedback " +
            "LEFT JOIN FETCH p.postContent " +
            "WHERE p.status = :status " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findByStatusOrderByCreatedAtDesc(@Param("status") PostStatus status, Pageable pageable);

    List<Post> findByCreatedByAndStatus(Long createdBy, PostStatus status);

    List<Post> findByCreatedByAndStatusOrderByCreatedAtDesc(Long userId, PostStatus status);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.schedule " +
            "LEFT JOIN FETCH p.requirement " +
            "LEFT JOIN FETCH p.reward " +
            "LEFT JOIN FETCH p.feedback " +
            "LEFT JOIN FETCH p.postContent " +
            "WHERE p.id = :postId")
    Optional<Post> findByIdWithAllDetails(@Param("postId") Long postId);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.schedule " +
            "LEFT JOIN FETCH p.requirement " +
            "LEFT JOIN FETCH p.reward " +
            "LEFT JOIN FETCH p.feedback " +
            "LEFT JOIN FETCH p.postContent " +
            "WHERE p.status = :status AND p.createdBy = :userId " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findByStatusAndCreatedBy(@Param("status") PostStatus status, @Param("userId") Long userId, Pageable pageable);

    Page<Post> findByCreatedByAndStatus(Long createdBy, PostStatus status, Pageable pageable);

    long countByCreatedBy(Long userId);

    Optional<Post> findFirstByCreatedByAndStatusOrderByCreatedAtDesc(Long userId, PostStatus status);

    List<Post> findByStatus(PostStatus postStatus);
}
