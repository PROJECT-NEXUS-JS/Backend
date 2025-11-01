package com.example.nexus.app.reward.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.reward.controller.doc.RewardControllerDoc;
import com.example.nexus.app.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/rewards")
@RequiredArgsConstructor
public class RewardController implements RewardControllerDoc {

    private final RewardService rewardService;

    @Override
    @PostMapping("/{participationId}/pay")
    public ResponseEntity<ApiResponse<Void>> payReward(
            @PathVariable Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        rewardService.payReward(participationId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
