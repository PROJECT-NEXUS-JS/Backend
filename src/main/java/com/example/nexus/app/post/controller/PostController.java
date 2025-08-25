package com.example.nexus.app.post.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.web.CookieService;
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

@Tag(name = "게시글", description = "게시글 관련 API")
@RestController
@RequestMapping("/v1/users/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CookieService cookieService;

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> createPost(@Valid @RequestPart("data") PostCreateRequest request,
                                                        @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
                                                        @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long postId = postService.createPost(request, thumbnailFile, imageFiles, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(postId));
    }

    @Operation(summary = "임시 저장", description = "게시글을 임시 저장합니다.")
    @PostMapping(value = "/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> saveDraft(@Valid @RequestPart("data") PostCreateRequest request,
                                                       @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
                                                       @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long postId = postService.saveDraft(request, thumbnailFile, imageFiles, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(postId));
    }

    @Operation(summary = "임시 게시글 수정 및 활성화", description = "임시 저장된 게시글을 수정하고 바로 활성화합니다.")
    @PatchMapping(value = "/{postId}/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateAndPublishDraft(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
            @Valid @RequestPart("data") PostUpdateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.updateAndPublishDraft(postId, request, thumbnailFile, imageFiles, userDetails);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "임시 게시글 활성화", description = "임시 저장된 게시글을 수정 없이 바로 활성화합니다.")
    @PatchMapping("/{postId}/activate")
    public ResponseEntity<ApiResponse<Void>> publishPost(@Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.publishPost(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(@Parameter(description = "게시글 ID", required = true)
                                                                   @PathVariable Long postId,
                                                                   HttpServletRequest httpServletRequest,
                                                                   HttpServletResponse httpServletResponse,
                                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean shouldIncrementView = cookieService.shouldIncrementViewAndSetCookie(postId, httpServletRequest, httpServletResponse);

        Long userId = userDetails != null ? userDetails.getUserId() : null;

        PostDetailResponse response = postService.findPost(postId, userId, shouldIncrementView);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "게시글 목록 조회 (필터 검색 가능)", description = "조건에 따라 게시글을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getPosts(
            @Parameter(description = "메인 카테고리 (WEB, APP, GAME, ETC)")
            @RequestParam(required = false) String mainCategory,
            @Parameter(description = "플랫폼 카테고리 (ANDROID, IOS, PC 등)")
            @RequestParam(required = false) String platformCategory,
            @Parameter(description = "장르 카테고리 (LIFESTYLE, EDUCATION, SOCIAL 등)")
            @RequestParam(required = false) String genreCategory,
            @Parameter(description = "검색 키워드")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "정렬 기준 (latest, popular, deadline, viewCount)")
            @RequestParam(defaultValue = "latest") String sortBy,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PostSummaryResponse> posts = postService.findPosts(mainCategory, platformCategory, genreCategory, keyword, sortBy, pageable);

        return ResponseEntity.ok(ApiResponse.onSuccess(posts));
    }

    @Operation(summary = "내 임시 저장 게시글 조회", description = "사용자의 임시 저장된 게시글을 조회합니다.")
    @GetMapping("/my/drafts")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getMyDrafts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PostSummaryResponse> posts = postService.findMyDrafts(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(posts));
    }

    @Operation(summary = "내 게시글 조회", description = "사용자가 작성한 활성 게시글을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PostSummaryResponse> posts = postService.findMyPosts(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(posts));
    }

    @Operation(summary = "게시글 수정", description = "게시글 정보를 수정합니다. 작성자만 수정 가능합니다.")
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @Valid @RequestPart("data") PostUpdateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        postService.updatePost(postId, request, thumbnailFile, imageFiles, userDetails);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. 작성자만 삭제 가능합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        postService.deletePost(postId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "게시글 상세 조회 (메인 뷰)", description = "게시글의 좌측 메인 뷰에 필요한 상세 정보를 조회합니다.")
    @GetMapping("/{postId}/main-view")
    public ResponseEntity<ApiResponse<PostMainViewDetailResponse>> getPostMainView(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId) {

        PostMainViewDetailResponse response = postService.findPostMainViewDetails(postId);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "유사 게시글 목록 조회", description = "특정 게시글과 유사한 게시글 목록을 조회합니다.")
    @GetMapping("/{postId}/similar")
    public ResponseEntity<ApiResponse<List<SimilarPostResponse>>> getSimilarPosts(
            @Parameter(description = "기준 게시글 ID", required = true)
            @PathVariable Long postId,
            @Parameter(description = "가져올 유사 게시글 최대 개수", example = "3")
            @RequestParam(defaultValue = "3") int limit) {

        List<SimilarPostResponse> similarPosts = postService.findSimilarPosts(postId, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(similarPosts));
    }

    @Operation(summary = "게시글 상세 조회 (우측 사이드바)", description = "게시글의 우측 사이드바에 필요한 상세 정보를 조회합니다.")
    @GetMapping("/{postId}/sidebar")
    public ResponseEntity<ApiResponse<PostRightSidebarResponse>> getPostRightSidebar(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId) {

        PostRightSidebarResponse response = postService.findPostRightSidebarDetails(postId);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
