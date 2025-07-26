package com.example.nexus.app.post.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.web.CookieService;
import com.example.nexus.app.post.controller.dto.request.PostCreateRequest;
import com.example.nexus.app.post.controller.dto.request.PostUpdateRequest;
import com.example.nexus.app.post.controller.dto.response.PostSummaryResponse;
import com.example.nexus.app.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글", description = "게시글 관련 API")
@RestController
@RequestMapping("/v1/users/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CookieService cookieService;

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(@Valid @RequestBody PostCreateRequest request,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long postId = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(postId));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostSummaryResponse>> getPost(@Parameter(description = "게시글 ID", required = true)
                                                                    @PathVariable Long postId,
                                                                    HttpServletRequest httpServletRequest,
                                                                    HttpServletResponse httpServletResponse,
                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean shouldIncrementView = cookieService.shouldIncrementViewAndSetCookie(postId, httpServletRequest, httpServletResponse);

        PostSummaryResponse response = postService.findPost(postId, shouldIncrementView);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "메인 카테고리별 게시글 조회", description = "메인 카테고리로 게시글을 필터링하여 조회합니다.")
    @GetMapping("/category/main/{mainCategory}")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getPostsByMainCategory(
            @Parameter(description = "메인 카테고리 (WEB, APP, GAME, 기타)", required = true)
            @PathVariable String mainCategory,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PostSummaryResponse> responses = postService.findPostsByMainCategory(mainCategory, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(responses));
    }

    @Operation(summary = "플랫폼 카테고리별 게시글 조회", description = "플랫폼 카테고리로게시글을 필터링하여 조회합니다.")
    @GetMapping("/category/platform/{platformCategory}")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getPostsByPlatformCategory(
            @Parameter(description = "플랫폼 카테고리", required = true)
            @PathVariable String platformCategory,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PostSummaryResponse> response =
                postService.findPostsByPlatformCategory(platformCategory, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "키워드 검색", description = "제목이나 내용에서 키워드로 게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> searchPosts(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PostSummaryResponse> responses = postService.findPostsByKeyword(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(responses));
    }

    @Operation(summary = "게시글 수정", description = "게시글 정보를 수정합니다. 작성자만 수정 가능합니다.")
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        postService.updatePost(postId, request, userDetails.getUserId());
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

    // TODO AWS S3로 변경

    private boolean shouldIncrementViewCount(Long postId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String cookieName = "viewed_post_" + postId;

        // 기존 쿠키 확인
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return false; // 이미 조회함
                }
            }
        }

        // 조회 기록 쿠키 설정
        Cookie viewCookie = new Cookie(cookieName, "viewed");
        viewCookie.setMaxAge(24 * 60 * 60); // 24시간
        viewCookie.setPath("/");
        viewCookie.setHttpOnly(true);
        httpServletResponse.addCookie(viewCookie);

        return true; // 조회수 증가 가능
    }
}
