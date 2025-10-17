package com.example.nexus.app.post.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.web.CookieService;
import com.example.nexus.app.post.controller.doc.PostControllerDoc;
import com.example.nexus.app.post.controller.dto.request.PostCreateRequest;
import com.example.nexus.app.post.controller.dto.request.PostUpdateRequest;
import com.example.nexus.app.post.controller.dto.response.PostDetailResponse;
import com.example.nexus.app.post.controller.dto.response.PostMainViewDetailResponse;
import com.example.nexus.app.post.controller.dto.response.PostRightSidebarResponse;
import com.example.nexus.app.post.controller.dto.response.PostSummaryResponse;
import com.example.nexus.app.post.controller.dto.response.SimilarPostResponse;
import com.example.nexus.app.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/users/posts")
@RequiredArgsConstructor
public class PostController implements PostControllerDoc {

    private final PostService postService;
    private final CookieService cookieService;

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> createPost(
            @Valid @RequestPart("data") PostCreateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long postId = postService.createPost(request, thumbnailFile, imageFiles, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(postId));
    }

    @Override
    @PostMapping(value = "/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> saveDraft(
            @Valid @RequestPart("data") PostCreateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long postId = postService.saveDraft(request, thumbnailFile, imageFiles, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(postId));
    }

    @Override
    @PatchMapping(value = "/{postId}/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateAndPublishDraft(
            @PathVariable Long postId,
            @Valid @RequestPart("data") PostUpdateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.updateAndPublishDraft(postId, request, thumbnailFile, imageFiles, userDetails);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Override
    @PatchMapping("/{postId}/activate")
    public ResponseEntity<ApiResponse<Void>> publishPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.publishPost(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Override
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(
            @PathVariable Long postId,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean shouldIncrementView = cookieService.shouldIncrementViewAndSetCookie(postId, httpServletRequest, httpServletResponse);

        Long userId = userDetails != null ? userDetails.getUserId() : null;

        PostDetailResponse response = postService.findPost(postId, userId, shouldIncrementView);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getPosts(
            @RequestParam(required = false) String mainCategory,
            @RequestParam(required = false) String platformCategory,
            @RequestParam(required = false) String genreCategory,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sortBy,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PostSummaryResponse> posts = postService.findPosts(mainCategory, platformCategory, genreCategory, keyword, sortBy, pageable);

        return ResponseEntity.ok(ApiResponse.onSuccess(posts));
    }

    @Override
    @GetMapping("/my/drafts")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getMyDrafts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PostSummaryResponse> posts = postService.findMyDrafts(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(posts));
    }

    @Override
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PostSummaryResponse> posts = postService.findMyPosts(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(posts));
    }

    @Override
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestPart("data") PostUpdateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        postService.updatePost(postId, request, thumbnailFile, imageFiles, userDetails);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Override
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        postService.deletePost(postId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.onSuccess(null));
    }

    @Override
    @GetMapping("/{postId}/main-view")
    public ResponseEntity<ApiResponse<PostMainViewDetailResponse>> getPostMainView(
            @PathVariable Long postId) {

        PostMainViewDetailResponse response = postService.findPostMainViewDetails(postId);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{postId}/similar")
    public ResponseEntity<ApiResponse<List<SimilarPostResponse>>> getSimilarPosts(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "3") int limit) {

        List<SimilarPostResponse> similarPosts = postService.findSimilarPosts(postId, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(similarPosts));
    }

    @Override
    @GetMapping("/{postId}/sidebar")
    public ResponseEntity<ApiResponse<PostRightSidebarResponse>> getPostRightSidebar(
            @PathVariable Long postId) {

        PostRightSidebarResponse response = postService.findPostRightSidebarDetails(postId);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
