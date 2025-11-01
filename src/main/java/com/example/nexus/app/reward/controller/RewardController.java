package com.example.nexus.app.reward.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.reward.controller.doc.RewardControllerDoc;
import com.example.nexus.app.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/posts/{postId}/rewards")
@RequiredArgsConstructor
public class RewardController implements RewardControllerDoc {

    private final RewardService rewardService;

    @Override
    @PostMapping("/{participationId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeParticipant(
            @PathVariable Long postId,
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        rewardService.completeParticipant(postId, participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Override
    @PostMapping("/{participationId}/pay")
    public ResponseEntity<ApiResponse<Void>> payReward(
            @PathVariable Long postId,
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        rewardService.payReward(postId, participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
