package com.example.nexus.app.post.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.post.controller.dto.response.PostLikeToggleResponse;
import com.example.nexus.app.post.controller.dto.response.PostSummaryResponse;
import com.example.nexus.app.post.service.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글 찜하기", description = "게시글 찜하기 관련 API")
@RestController
@RequestMapping("/v1/users/posts")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @Operation(summary = "게시글 찜하기/찜하기 취소", description = "게시글을 찜하거나 찜을 취소합니다.")
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<PostLikeToggleResponse>> toggleLike(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PostLikeToggleResponse response = postLikeService.toggleLike(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "내 찜 목록 조회", description = "사용자가 찜한 게시글 목록을 조회합니다.")
    @GetMapping("/likes")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getUserLikes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PostSummaryResponse> likes = postLikeService.findUserLike(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(likes));
    }

    @Operation(summary = "찜하기 상태 확인", description = "특정 게시글의 찜하기 상태를 확인합니다.")
    @GetMapping("/{postId}/like/status")
    public ResponseEntity<ApiResponse<Boolean>> getLikeStatus(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean isLiked = postLikeService.isLiked(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(isLiked));
    }

    @Operation(summary = "게시글 찜 개수 조회", description = "특정 게시글의 총 찜 개수를 조회합니다.")
    @GetMapping("/{postId}/like/count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId) {

        Long likeCount = postLikeService.getLikeCount(postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(likeCount));
    }
}
