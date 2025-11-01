package com.example.nexus.app.reward.controller.doc;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Reward", description = "리워드 관리 API")
public interface RewardControllerDoc {

    @Operation(
            summary = "참여자 완료 처리",
            description = "특정 참여자의 테스트를 완료 처리합니다"
    )
    ResponseEntity<ApiResponse<Void>> completeParticipant(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @PathVariable @Schema(description = "참여 ID") Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "리워드 지급 처리",
            description = "특정 참여자에게 리워드를 지급 처리합니다"
    )
    ResponseEntity<ApiResponse<Void>> payReward(
            @PathVariable @Schema(description = "게시글 ID") Long postId,
            @PathVariable @Schema(description = "참여 ID") Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
