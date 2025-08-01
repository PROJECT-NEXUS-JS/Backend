package com.example.nexus.app.post.service;

import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.s3.S3UploadService;
import com.example.nexus.app.post.controller.dto.PostSearchCondition;
import com.example.nexus.app.post.controller.dto.request.PostCreateRequest;
import com.example.nexus.app.post.controller.dto.request.PostUpdateRequest;
import com.example.nexus.app.post.controller.dto.response.PostDetailResponse;
import com.example.nexus.app.post.domain.*;
import com.example.nexus.app.post.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostScheduleRepository postScheduleRepository;
    private final PostRequirementRepository postRequirementRepository;
    private final PostRewardRepository postRewardRepository;
    private final PostFeedbackRepository postFeedbackRepository;
    private final PostContentRepository postContentRepository;
    private final S3UploadService s3UploadService;
    private final PostUserStatusService postUserStatusService;

    @Transactional
    public Long createPost(PostCreateRequest request, MultipartFile thumbnailFile, CustomUserDetails userDetails) {
        String thumbnailUrl = null;
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            thumbnailUrl = s3UploadService.uploadFile(thumbnailFile);
        }

        Post post = request.toPostEntity();

        if (thumbnailUrl != null) {
            post.updateBasicInfo(post.getTitle(), post.getServiceSummary(), post.getCreatorIntroduction(), post.getDescription(), thumbnailUrl);
        }

        Post savedPost = postRepository.save(post);
        createAndSaveRelatedEntities(request, savedPost);

        return savedPost.getId();
    }

    @Transactional
    public Long saveDraft(PostCreateRequest request, MultipartFile thumbnailFile, CustomUserDetails userDetails) {
        String thumbnailUrl = null;
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            thumbnailUrl = s3UploadService.uploadFile(thumbnailFile);
        }

        Post post = request.toPostEntity(PostStatus.DRAFT);

        if (thumbnailUrl != null) {
            post.updateBasicInfo(post.getTitle(), post.getServiceSummary(), post.getCreatorIntroduction(), post.getDescription(), thumbnailUrl);
        }

        Post savedPost = postRepository.save(post);
        createAndSaveRelatedEntities(request, savedPost);

        return savedPost.getId();
    }

    @Transactional
    public void updateAndPublishDraft(Long postId, PostUpdateRequest request, MultipartFile thumbnailFile, CustomUserDetails userDetails) {
        Post post = getPostWithDetail(postId);
        validateOwnership(post, userDetails.getUserId());
        
        if (!post.isDraft()) {
            throw new GeneralException(ErrorStatus.POST_NOT_DRAFT);
        }

        String newThumbnailUrl = post.getThumbnailUrl();
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            newThumbnailUrl = s3UploadService.uploadFile(thumbnailFile);
        }

        post.updateBasicInfo(request.title(), request.serviceSummary(), request.creatorIntroduction(), request.description(), newThumbnailUrl);
        post.updateMainCategories(request.mainCategory());
        post.updatePlatformCategories(request.platformCategory());
        post.updateGenreCategories(request.genreCategories());

        updateRelatedEntities(request, post);
        
        validatePostForPublishing(post);
        post.active();
    }

    @Transactional
    public void publishPost(Long postId, Long userId) {
        System.out.println("=== Starting publishPost - Post ID: " + postId + ", User ID: " + userId);
        Post post = getPostWithDetail(postId);
        validateOwnership(post, userId);
        
        // 디버깅: 실제 상태 확인
        if (!post.isDraft()) {
            throw new GeneralException(ErrorStatus.POST_NOT_DRAFT);
        }
        
        validatePostForPublishing(post);
        post.active();
    }

    @Transactional
    public PostDetailResponse findPost(Long postId, Long userId, boolean incrementView) {
        Post post = getPostWithDetail(postId);

        // DRAFT 상태인 경우 작성자만 조회 가능
        if (post.isDraft() && !post.isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }

        if (incrementView && post.isActive()) {
            post.incrementViewCount();
        }

        PostUserStatusService.PostUserStatus status = postUserStatusService.getPostUserStatus(postId, userId);
        return PostDetailResponse.from(post, status.isLiked(), status.isParticipated());
    }


    public Page<PostDetailResponse> findMyDrafts(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findByStatusAndCreatedBy(PostStatus.DRAFT, userId, pageable);
        return mapPostsWithUserStatus(posts, userId);
    }

    public Page<PostDetailResponse> findMyPosts(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findByStatusAndCreatedBy(PostStatus.ACTIVE, userId, pageable);
        return mapPostsWithUserStatus(posts, userId);
    }

    public Page<PostDetailResponse> findPosts(String mainCategory, String platformCategory,
                                              String genreCategory, String keyword, String sortBy, Long userId, Pageable pageable) {
        PostSearchCondition condition = PostSearchCondition.builder()
                .mainCategory(parseMainCategory(mainCategory))
                .platformCategory(parsePlatformCategory(platformCategory))
                .genreCategory(parseGenreCategory(genreCategory))
                .keyword(keyword)
                .sortBy(sortBy)
                .status(PostStatus.ACTIVE)
                .build();

        Page<Post> posts = postRepository.findPostWithCondition(condition, pageable);
        return mapPostsWithUserStatus(posts, userId);
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, MultipartFile thumbnailFile, CustomUserDetails userDetails) {
        Post post = getPost(postId);
        validateOwnership(post, userDetails.getUserId());

        String newThumbnailUrl = post.getThumbnailUrl();
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            // 기존 이미지 삭제 (선택사항)
//            if (post.getThumbnailUrl() != null) {
//                deleteS3Image(post.getThumbnailUrl());
//            }
            newThumbnailUrl = s3UploadService.uploadFile(thumbnailFile);
        }

        post.updateBasicInfo(request.title(), request.serviceSummary(), request.creatorIntroduction(), request.description(), newThumbnailUrl);
        post.updateMainCategories(request.mainCategory());
        post.updatePlatformCategories(request.platformCategory());
        post.updateGenreCategories(request.genreCategories());

        updateRelatedEntities(request, post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = getPostWithValidation(postId, userId);
        postRepository.delete(post);
    }

    private void createAndSaveRelatedEntities(PostCreateRequest request, Post post) {
        PostSchedule schedule = request.toPostScheduleEntity(post);
        postScheduleRepository.save(schedule);

        PostRequirement requirement = request.toPostRequirementEntity(post);
        postRequirementRepository.save(requirement);

        if (request.rewardType() != null) {
            PostReward reward = request.toPostRewardEntity(post);
            postRewardRepository.save(reward);
        }

        PostFeedback feedback = request.toPostFeedbackEntity(post);
        postFeedbackRepository.save(feedback);

        PostContent content = request.toPostContentEntity(post);
        postContentRepository.save(content);
    }

    private void updateRelatedEntities(PostUpdateRequest request, Post post) {
        PostSchedule schedule = postScheduleRepository.findByPostId(post.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_SCHEDULE_NOT_FOUND));
        schedule.update(request.startDate(), request.endDate(), 
                       request.recruitmentDeadline(), request.durationTime());

        PostRequirement requirement = postRequirementRepository.findByPostId(post.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_REQUIREMENT_NOT_FOUND));
        requirement.update(request.maxParticipants(), request.genderRequirement(),
                          request.ageMin(), request.ageMax(), request.additionalRequirements());

        PostReward reward = postRewardRepository.findByPostId(post.getId()).orElse(null);
        if (request.rewardType() != null) {
            if (reward == null) {
                reward = request.toPostRewardEntity(post);
                postRewardRepository.save(reward);
            } else {
                reward.update(request.rewardType(), request.rewardDescription());
            }
        } else if (reward != null) {
            postRewardRepository.delete(reward);
        }

        PostFeedback feedback = postFeedbackRepository.findByPostId(post.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_FEEDBACK_NOT_FOUND));
        feedback.update(request.feedbackMethod(), request.feedbackItems(), 
                       request.privacyCollectionItems());

        PostContent content = postContentRepository.findByPostId(post.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_CONTENT_NOT_FOUND));
        content.update(request.participationMethod(), request.storyGuide(), request.mediaUrl());
    }

    private void validatePostForPublishing(Post post) {
        if (post.getSchedule() == null) {
            throw new GeneralException(ErrorStatus.POST_SCHEDULE_REQUIRED);
        }
        if (post.getRequirement() == null) {
            throw new GeneralException(ErrorStatus.POST_REQUIREMENT_REQUIRED);
        }
        if (post.getFeedback() == null) {
            throw new GeneralException(ErrorStatus.POST_FEEDBACK_REQUIRED);
        }
        if (post.getPostContent() == null) {
            throw new GeneralException(ErrorStatus.POST_CONTENT_REQUIRED);
        }
    }

    private Post getPostWithValidation(Long postId, Long userId) {
        Post post = getPost(postId);
        validateOwnership(post, userId);
        return post;
    }

    private void validateOwnership(Post post, Long userId) {
        if (!post.isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
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

    private GenreCategory parseGenreCategory(String genreCategory) {
        if (genreCategory == null || genreCategory.isBlank()) {
            return null;
        }
        try {
            return GenreCategory.valueOf(genreCategory.toUpperCase());  
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus.INVALID_GENRE_CATEGORY);
        }
    }

    private Post getPostWithDetail(Long postId) {
        Post post = postRepository.findByIdWithAllDetails(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        return post;
    }

    private Page<PostDetailResponse> mapPostsWithUserStatus(Page<Post> posts, Long userId) {
        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();

        Map<Long, PostUserStatusService.PostUserStatus> statusMap = postUserStatusService.getPostUserStatuses(postIds, userId);

        return posts.map(post -> {
            PostUserStatusService.PostUserStatus status = statusMap.get(post.getId());
            return PostDetailResponse.from(post, status.isLiked(), status.isParticipated());
        });
    }
}
