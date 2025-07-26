package com.example.nexus.app.post.controller.dto;

import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.post.domain.PostStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostSearchCondition {
    private MainCategory mainCategory;
    private PlatformCategory platformCategory;
    private String keyword;
    private String sortBy;
    private PostStatus status;
}
