package com.example.nexus.app.post.service;

import com.example.nexus.app.badge.domain.BadgeConditionType;
import com.example.nexus.app.badge.service.BadgeService;
import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.s3.S3UploadService;
import com.example.nexus.app.mypage.service.RecentViewedPostService;
import com.example.nexus.app.post.controller.dto.PostSearchCondition;
import com.example.nexus.app.post.controller.dto.request.PostCreateRequest;
import com.example.nexus.app.post.controller.dto.request.PostUpdateRequest;
import com.example.nexus.app.post.controller.dto.response.PostDetailResponse;
import com.example.nexus.app.post.controller.dto.response.PostMainViewDetailResponse;
import com.example.nexus.app.post.controller.dto.response.PostRightSidebarResponse;
import com.example.nexus.app.post.controller.dto.response.PostSummaryResponse;
import com.example.nexus.app.post.controller.dto.response.SimilarPostResponse;
import com.example.nexus.app.post.domain.*;
import com.example.nexus.app.post.repository.*;
import com.example.nexus.app.post.service.dto.PostUserStatus;
import com.example.nexus.app.reward.domain.PostReward;
import com.example.nexus.app.reward.domain.RewardType;
import com.example.nexus.app.reward.repository.PostRewardRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final ViewCountService viewCountService;
    private final UserRepository userRepository;
    private final RecentViewedPostService recentViewedPostService;
    private final BadgeService badgeService;

    @Transactional
    public Long createPost(PostCreateRequest request, MultipartFile thumbnailFile, List<MultipartFile> imageFiles, CustomUserDetails userDetails) {
        Post post = createPostWithThumbnailAndImage(request, thumbnailFile, imageFiles, PostStatus.ACTIVE);
        Post savedPost = postRepository.save(post);
        createAndSaveRelatedEntitiesWithImage(request, savedPost, imageFiles);

        // 뱃지 부여 체크 - 플래너 뱃지 (테스트 모집)
        badgeService.checkAndAwardBadge(userDetails.getUserId(), BadgeConditionType.POST_PUBLISHED);

        return savedPost.getId();
    }

    @Transactional
    public Long saveDraft(PostCreateRequest request, MultipartFile thumbnailFile, List<MultipartFile> imageFiles, CustomUserDetails userDetails) {
        Post post = createPostWithThumbnailAndImage(request, thumbnailFile, imageFiles, PostStatus.DRAFT);
        Post savedPost = postRepository.save(post);
        createAndSaveRelatedEntitiesWithImage(request, savedPost, imageFiles);

        return savedPost.getId();
    }

    @Transactional
    public void updateAndPublishDraft(Long postId, PostUpdateRequest request, MultipartFile thumbnailFile,
                                      List<MultipartFile> imageFiles, CustomUserDetails userDetails) {
        Post post = getPostWithDetail(postId);
        validateOwnership(post, userDetails.getUserId());

        if (!post.isDraft()) {
            throw new GeneralException(ErrorStatus.POST_NOT_DRAFT);
        }

        // Draft를 Active로 변경 전에 상태 확인
        boolean wasDraft = post.isDraft();

        if (request.title() != null) {
            post.updateTitle(request.title());
        }
        if (request.serviceSummary() != null) {
            post.updateServiceSummary(request.serviceSummary());
        }
        if (request.creatorIntroduction() != null) {
            post.updateCreatorIntroduction(request.creatorIntroduction());
        }
        if (request.description() != null) {
            post.updateDescription(request.description());
        }
        if (request.qnaMethod() != null) {
            post.updateQnaMethod(request.qnaMethod());
        }
        if (request.teamMemberCount() != null) {
            post.updateTeamMemberCount(request.teamMemberCount());
        }
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String thumbnailUrl = s3UploadService.uploadFile(thumbnailFile);
            post.updateThumbnailUrl(thumbnailUrl);
        }

        if (request.mainCategory() != null) {
            post.updateMainCategories(request.mainCategory());
        }
        if (request.platformCategory() != null) {
            post.updatePlatformCategories(request.platformCategory());
        }
        if (request.genreCategories() != null) {
            post.updateGenreCategories(request.genreCategories());
        }

        updateRelatedEntitiesWithImage(request, post, imageFiles);

        validatePostForPublishing(post);
        post.active();

        // 뱃지 부여 체크 - 플래너 뱃지 (Draft를 Active로 변경하여 테스트 모집)
        if (wasDraft) {
            badgeService.checkAndAwardBadge(userDetails.getUserId(), BadgeConditionType.POST_PUBLISHED);
        }
    }

    @Transactional
    public void publishPost(Long postId, Long userId) {
        Post post = getPostWithDetail(postId);
        validateOwnership(post, userId);

        if (!post.isDraft()) {
            throw new GeneralException(ErrorStatus.POST_NOT_DRAFT);
        }

        validatePostForPublishing(post);
        post.active();

        // 뱃지 부여 체크 - 플래너 뱃지 (Draft를 Active로 변경하여 테스트 모집)
        badgeService.checkAndAwardBadge(userId, BadgeConditionType.POST_PUBLISHED);
    }

    @Transactional
    public PostDetailResponse findPost(Long postId, Long userId, boolean incrementView) {
        Post post = getPostWithDetail(postId);

        if (post.isDraft() && !post.isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }

        if (incrementView && post.isActive()) {
            viewCountService.incrementViewCount(postId);
        }

        if (userId != null) {
            recentViewedPostService.saveRecentView(userId, postId);
        }

        PostUserStatus status = postUserStatusService.getPostUserStatus(postId, userId);
        Long currentViewCount = viewCountService.getTotalViewCount(postId);

        String creatorProfileUrl = userRepository.findById(post.getCreatedBy())
                .map(User::getProfileUrl)
                .orElse(null);

        return PostDetailResponse.from(post, status.isLiked(), status.isParticipated(), currentViewCount,
                creatorProfileUrl, status.participationStatus());
    }


    public Page<PostSummaryResponse> findMyDrafts(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findByStatusAndCreatedBy(PostStatus.DRAFT, userId, pageable);
        return posts.map(PostSummaryResponse::from);
    }

    public Page<PostSummaryResponse> findMyPosts(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findByStatusAndCreatedBy(PostStatus.ACTIVE, userId, pageable);
        return posts.map(PostSummaryResponse::from);
    }

    public Page<PostSummaryResponse> findPosts(String mainCategory, String platformCategory,
                                               String genreCategory, String keyword, String sortBy, Pageable pageable) {
        PostSearchCondition condition = PostSearchCondition.builder()
                .mainCategory(parseMainCategory(mainCategory))
                .platformCategory(parsePlatformCategory(platformCategory))
                .genreCategory(parseGenreCategory(genreCategory))
                .keyword(keyword)
                .sortBy(sortBy)
                .status(PostStatus.ACTIVE)
                .build();

        Page<Post> posts = postRepository.findPostWithCondition(condition, pageable);
        return posts.map(PostSummaryResponse::from);
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, MultipartFile thumbnailFile,
                           List<MultipartFile> imageFiles, CustomUserDetails userDetails) {
        Post post = getPostWithDetail(postId);
        validateOwnership(post, userDetails.getUserId());

        if (request.title() != null) {
            post.updateTitle(request.title());
        }
        if (request.serviceSummary() != null) {
            post.updateServiceSummary(request.serviceSummary());
        }
        if (request.creatorIntroduction() != null) {
            post.updateCreatorIntroduction(request.creatorIntroduction());
        }
        if (request.description() != null) {
            post.updateDescription(request.description());
        }
        if (request.qnaMethod() != null) {
            post.updateQnaMethod(request.qnaMethod());
        }
        if (request.teamMemberCount() != null) {
            post.updateTeamMemberCount(request.teamMemberCount());
        }
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String thumbnailUrl = s3UploadService.uploadFile(thumbnailFile);
            post.updateThumbnailUrl(thumbnailUrl);
        }

        if (request.mainCategory() != null) {
            post.updateMainCategories(request.mainCategory());
        }
        if (request.platformCategory() != null) {
            post.updatePlatformCategories(request.platformCategory());
        }
        if (request.genreCategories() != null) {
            post.updateGenreCategories(request.genreCategories());
        }

        updateRelatedEntitiesWithImage(request, post, imageFiles);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = getPostWithValidation(postId, userId);
        
        recentViewedPostService.deleteByPostId(postId);
        
        postRepository.delete(post);
    }

    private void createAndSaveRelatedEntitiesWithImage(PostCreateRequest request, Post post, List<MultipartFile> imageFiles) {
        PostSchedule schedule = PostSchedule.builder()
                .post(post)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .recruitmentDeadline(request.recruitmentDeadline())
                .durationTime(request.durationTime())
                .build();
        postScheduleRepository.save(schedule);

        PostRequirement requirement = PostRequirement.builder()
                .post(post)
                .maxParticipants(request.maxParticipants())
                .genderRequirement(request.genderRequirement())
                .ageMin(request.ageMin())
                .ageMax(request.ageMax())
                .additionalRequirements(request.additionalRequirements())
                .build();
        postRequirementRepository.save(requirement);

        if (request.rewardType() != null) {
            PostReward reward = PostReward.builder()
                    .post(post)
                    .rewardType(request.rewardType())
                    .rewardDescription(request.rewardDescription())
                    .build();
            postRewardRepository.save(reward);
        }

        PostFeedback feedback = PostFeedback.builder()
                .post(post)
                .feedbackMethod(request.feedbackMethod())
                .feedbackItems(request.feedbackItems())
                .privacyItems(request.privacyItems())
                .privacyPurpose(request.privacyPurpose())
                .build();
        postFeedbackRepository.save(feedback);

        // 이미지 파일들 업로드 처리
        List<String> uploadedImageUrls = uploadImagesIfPresent(imageFiles);
        List<String> finalMediaUrls = new ArrayList<>();

        if (!uploadedImageUrls.isEmpty()) {
            finalMediaUrls = uploadedImageUrls;
        } else if (request.mediaUrl() != null) {
            finalMediaUrls.add(request.mediaUrl());
        }

        PostContent content = PostContent.builder()
                .post(post)
                .participationMethod(request.participationMethod())
                .storyGuide(request.storyGuide())
                .mediaUrls(finalMediaUrls)
                .build();
        postContentRepository.save(content);
    }

    private void updateRelatedEntitiesWithImage(PostUpdateRequest request, Post post, List<MultipartFile> imageFiles) {
        updateSchedule(request, post.getSchedule());
        updateRequirement(request, post.getRequirement());
        updateReward(request, post);
        updateFeedback(request, post.getFeedback());
        updateContent(request, post.getPostContent(), imageFiles);
    }

    private void updateSchedule(PostUpdateRequest request, PostSchedule schedule) {
        if (schedule == null) {
            return;
        }

        if (request.startDate() != null) {
            schedule.updateStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            schedule.updateEndDate(request.endDate());
        }
        if (request.recruitmentDeadline() != null) {
            schedule.updateRecruitmentDeadline(request.recruitmentDeadline());
        }
        if (request.durationTime() != null) {
            schedule.updateDurationTime(request.durationTime());
        }
    }

    private void updateRequirement(PostUpdateRequest request, PostRequirement requirement) {
        if (requirement == null) {
            return;
        }

        if (request.maxParticipants() != null) {
            requirement.updateMaxParticipants(request.maxParticipants());
        }
        if (request.genderRequirement() != null) {
            requirement.updateGenderRequirement(request.genderRequirement());
        }
        if (request.ageMin() != null) {
            requirement.updateAgeMin(request.ageMin());
        }
        if (request.ageMax() != null) {
            requirement.updateAgeMax(request.ageMax());
        }
        if (request.additionalRequirements() != null) {
            requirement.updateAdditionalRequirements(request.additionalRequirements());
        }
    }

    private void updateReward(PostUpdateRequest request, Post post) {
        PostReward reward = post.getReward();

        if (reward == null && request.rewardType() == null) {
            return;
        }

        if (reward == null) {
            reward = PostReward.builder()
                    .post(post)
                    .rewardType(request.rewardType())
                    .rewardDescription(request.rewardDescription())
                    .build();
            postRewardRepository.save(reward);
            return;
        }

        if (request.rewardType() == null) {
            postRewardRepository.delete(reward);
            return;
        }

        if (request.rewardType() != null) {
            reward.updateRewardType(request.rewardType());
        }
        if (request.rewardDescription() != null) {
            reward.updateRewardDescription(request.rewardDescription());
        }
    }

    private void updateFeedback(PostUpdateRequest request, PostFeedback feedback) {
        if (feedback == null) {
            return;
        }

        if (request.feedbackMethod() != null) {
            feedback.updateFeedbackMethod(request.feedbackMethod());
        }
        if (request.feedbackItems() != null) {
            feedback.updateFeedbackItems(request.feedbackItems());
        }
        if (request.privacyItems() != null) {
            feedback.updatePrivacyItems(request.privacyItems());
        }
        if (request.privacyPurpose() != null) {
            feedback.updatePrivacyPurpose(request.privacyPurpose());
        }
    }

    private void updateContent(PostUpdateRequest request, PostContent content, List<MultipartFile> imageFiles) {
        if (content == null) {
            return;
        }

        if (request.participationMethod() != null) {
            content.updateParticipationMethod(request.participationMethod());
        }
        if (request.storyGuide() != null) {
            content.updateStoryGuide(request.storyGuide());
        }

        List<String> uploadedImageUrls = uploadImagesIfPresent(imageFiles);
        if (!uploadedImageUrls.isEmpty()) {
            content.updateMediaUrls(uploadedImageUrls);
        } else if (request.mediaUrl() != null) {
            content.updateMediaUrls(List.of(request.mediaUrl()));
        }
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
        return postRepository.findByIdWithAllDetails(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }

    private Post createPostWithThumbnailAndImage(PostCreateRequest request, MultipartFile thumbnailFile,
                                                 List<MultipartFile> imageFiles, PostStatus status) {
        String thumbnailUrl = uploadThumbnailIfPresent(thumbnailFile, null);
        Post post = (status == PostStatus.DRAFT) ? request.toPostEntity(PostStatus.DRAFT) : request.toPostEntity();

        if (thumbnailUrl != null) {
            post.updateThumbnailUrl(thumbnailUrl);
        }
        return post;
    }

    private String uploadThumbnailIfPresent(MultipartFile thumbnailFile, String currentThumbnailUrl) {
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
//            기존 이미지 삭제 (선택사항)
//            if (post.getThumbnailUrl() != null) {
//                deleteS3Image(post.getThumbnailUrl());
//            }
            return s3UploadService.uploadFile(thumbnailFile);
        }
        return currentThumbnailUrl;
    }

    private List<String> uploadImagesIfPresent(List<MultipartFile> imageFiles) {
        List<String> uploadedUrls = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    String uploadedUrl = s3UploadService.uploadFile(imageFile);
                    uploadedUrls.add(uploadedUrl);
                }
            }
        }
        return uploadedUrls;
    }

    public PostMainViewDetailResponse findPostMainViewDetails(Long postId) {
        Post post = postRepository.findByIdWithAllDetails(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // ACTIVE 상태가 아닌 게시글은 조회 불가
        if (!post.isActive()) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }

        return PostMainViewDetailResponse.from(post);
    }

    public List<SimilarPostResponse> findSimilarPosts(Long postId, int limit) {
        Post basePost = postRepository.findByIdWithAllDetails(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // ACTIVE 상태가 아닌 게시글은 유사 게시글 검색 기준에서 제외
        if (!basePost.isActive()) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }

        Set<MainCategory> baseMainCategories = basePost.getMainCategory();
        Set<GenreCategory> baseGenreCategories = new HashSet<>(basePost.getGenreCategories());
        boolean baseRewardProvided =
                basePost.getReward() != null && basePost.getReward().getRewardType() != RewardType.NONE;

        List<Post> similarPosts = postRepository.findAll().stream()
                .filter(post -> !post.getId().equals(postId))
                .filter(Post::isActive)
                .filter(post -> {
                    boolean categoryMatch = !post.getMainCategory().stream().filter(baseMainCategories::contains)
                            .collect(Collectors.toSet()).isEmpty() ||
                            !post.getGenreCategories().stream().filter(baseGenreCategories::contains).collect(
                                    Collectors.toSet()).isEmpty();

                    boolean rewardMatch =
                            (post.getReward() != null && post.getReward().getRewardType() != RewardType.NONE)
                                    == baseRewardProvided;

                    return categoryMatch && rewardMatch;
                })
                .limit(limit)
                .toList();

        return similarPosts.stream()
                .map(SimilarPostResponse::from)
                .collect(Collectors.toList());
    }

    public PostRightSidebarResponse findPostRightSidebarDetails(Long postId) {
        Post post = postRepository.findByIdWithAllDetails(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // ACTIVE 상태가 아닌 게시글은 조회 불가
        if (!post.isActive()) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }

        Long createdByUserId = post.getCreatedBy();
        Optional<User> creatorUser = userRepository.findById(createdByUserId);

        return PostRightSidebarResponse.from(post, creatorUser.orElse(null));
    }
}
