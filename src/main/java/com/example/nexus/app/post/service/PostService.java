package com.example.nexus.app.post.service;

import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.category.repository.GenreCategoryRepository;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.post.controller.dto.PostSearchCondition;
import com.example.nexus.app.post.controller.dto.request.PostCreateRequest;
import com.example.nexus.app.post.controller.dto.request.PostUpdateRequest;
import com.example.nexus.app.post.controller.dto.response.PostSummaryResponse;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.PostStatus;
import com.example.nexus.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final GenreCategoryRepository genreCategoryRepository;

    @Transactional
    public Long createPost(PostCreateRequest request) {
        List<GenreCategory> genreCategories = getGenreCategories(request.genreCategoryIds());

        Post post = request.toEntity();
        return postRepository.save(post).getId();
    }

    @Transactional
    public PostSummaryResponse findPost(Long postId, boolean incrementView) {
        Post post = getPost(postId);
        if (incrementView) {
            post.incrementViewCount();
        }
        return PostSummaryResponse.from(post);
    }

    public Page<PostSummaryResponse> findAllPosts(Pageable pageable) {
        return postRepository.findByStatusOrderByCreatedAtDesc(PostStatus.ACTIVE, pageable)
                .map(PostSummaryResponse::from);
    }

    public Page<PostSummaryResponse> findPosts(String mainCategory, String platformCategory,
                                               String keyword, String sortBy, Pageable pageable) {
        PostSearchCondition condition = PostSearchCondition.builder()
                .mainCategory(parseMainCategory(mainCategory))
                .platformCategory(parsePlatformCategory(platformCategory))
                .keyword(keyword)
                .sortBy(sortBy)
                .status(PostStatus.ACTIVE)
                .build();

        return postRepository.findPostWithCondition(condition, pageable)
                .map(PostSummaryResponse::from);
    }


    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = getPost(postId);
        validateOwnership(post, userId);

        List<GenreCategory> genreCategories = getGenreCategories(request.genreCategoryIds());
        request.updateEntity(post, genreCategories);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = getPost(postId);
        validateOwnership(post, userId);

        postRepository.delete(post);
    }

    private List<GenreCategory> getGenreCategories(List<Long> genreCategoryIds) {
        if (genreCategoryIds == null || genreCategoryIds.isEmpty()) {
            return List.of();
        }

        List<GenreCategory> genreCategories = genreCategoryRepository.findByIdIn(genreCategoryIds);

        if (genreCategories.size() != genreCategoryIds.size()) {
            throw new GeneralException(ErrorStatus.GENRE_CATEGORY_NOT_FOUND);
        }

        return genreCategories;
    }

    private void validateOwnership(Post post, Long userId) {
        if (!post.isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }
    }

    private Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        return post;
    }

    private MainCategory parseMainCategory(String mainCategory) {
        if (mainCategory == null || mainCategory.isBlank()) {
            return null;
        }

        try {
            return MainCategory.valueOf(mainCategory.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus.INVALID_MAIN_CATEGORY);
        }
    }

    private PlatformCategory parsePlatformCategory(String platformCategory) {
        if (platformCategory == null || platformCategory.isBlank()) {
            return null;
        }

        try {
            return PlatformCategory.valueOf(platformCategory.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus.INVALID_PLATFORM_CATEGORY);
        }
    }
}
