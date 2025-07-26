package com.example.nexus.app.post.service;

import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.category.repository.GenreCategoryRepository;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.s3.S3UploadService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final GenreCategoryRepository genreCategoryRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public Long createPost(PostCreateRequest request, MultipartFile thumbnailFile, CustomUserDetails userDetails) {
        List<GenreCategory> genreCategories = getGenreCategories(request.genreCategoryIds());

        String thumbnailUrl = null;
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            thumbnailUrl = s3UploadService.uploadFile(thumbnailFile);
        }

        Post post = request.toEntity();

        if (thumbnailUrl != null) {
            post.updateThumbnailUrl(thumbnailUrl);
        }

        genreCategories.forEach(post::addGenreCategory);
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
    public void updatePost(Long postId, PostUpdateRequest request, MultipartFile thumbnailFile, CustomUserDetails userDetails) {
        Post post = getPost(postId);
        validateOwnership(post, userDetails.getUserId());

        List<GenreCategory> genreCategories = getGenreCategories(request.genreCategoryIds());

        String newThumbnailUrl = post.getThumbnailUrl();
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            // 기존 이미지 삭제 (선택사항)
//            if (post.getThumbnailUrl() != null) {
//                deleteS3Image(post.getThumbnailUrl());
//            }
            newThumbnailUrl = s3UploadService.uploadFile(thumbnailFile);
        }


        request.updateEntity(post, genreCategories, newThumbnailUrl);
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

    private void deleteS3Image(String imageUrl) {
        try {
            s3UploadService.deleteFile(imageUrl);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.S3_DELETE_FAILED);
        }
    }
}
