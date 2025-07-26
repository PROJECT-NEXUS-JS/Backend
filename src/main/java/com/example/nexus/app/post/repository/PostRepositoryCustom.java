package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.controller.dto.PostSearchCondition;
import com.example.nexus.app.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> findPostWithCondition(PostSearchCondition condition, Pageable pageable);
}
