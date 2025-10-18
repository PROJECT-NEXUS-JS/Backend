package com.example.nexus.app.post.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.post.controller.doc.PostLikeControllerDoc;
import com.example.nexus.app.post.controller.dto.response.PostDetailResponse;
import com.example.nexus.app.post.controller.dto.response.PostLikeToggleResponse;
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

@RestController
@RequestMapping("/v1/users/posts")
@RequiredArgsConstructor
public class PostLikeController implements PostLikeControllerDoc {

    private final PostLikeService postLikeService;

    @Override
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<PostLikeToggleResponse>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PostLikeToggleResponse response = postLikeService.toggleLike(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/likes")
    public ResponseEntity<ApiResponse<Page<PostDetailResponse>>> getUserLikes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PostDetailResponse> likes = postLikeService.findUserLike(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(likes));
    }

    @Override
    @GetMapping("/{postId}/like/status")
    public ResponseEntity<ApiResponse<Boolean>> getLikeStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean isLiked = postLikeService.isLiked(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(isLiked));
    }

    @Override
    @GetMapping("/{postId}/like/count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(
            @PathVariable Long postId) {

        Long likeCount = postLikeService.getLikeCount(postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(likeCount));
    }
}
