package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom{

    Page<Post> findByStatusOrderByCreatedAtDesc(PostStatus status, Pageable pageable);

    // 상세 조회 (상태 확인 포함)
    Optional<Post> findByIdAndStatus(Long id, PostStatus status);

    // 사용자가 작성한 게시글
    List<Post> findByCreatedByAndStatusOrderByCreatedAtDesc(Long userId, PostStatus status);
}
