package com.example.nexus.app.reward.controller.doc;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "리워드", description = "리워드 관리 API")
public interface RewardControllerDoc {

    @Operation(
            summary = "리워드 지급 처리",
            description = "특정 참여자에게 리워드를 지급 처리합니다"
    )
    ResponseEntity<ApiResponse<Void>> payReward(
            @PathVariable @Schema(description = "참여 ID") Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
