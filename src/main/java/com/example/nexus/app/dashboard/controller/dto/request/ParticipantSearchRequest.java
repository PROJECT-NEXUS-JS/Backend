package com.example.nexus.app.dashboard.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "참여자 목록 조회 검색 조건")
public class ParticipantSearchRequest {

    @Schema(description = "참여 상태 필터 (PENDING, APPROVED, COMPLETED)", example = "COMPLETED")
    private String status;

    @Schema(description = "리워드 지급 상태 필터 (PENDING, PAID)")
    private String rewardStatus;

    @Schema(description = "닉네임 검색")
    private String nickname;

    @Schema(description = "정렬 기준 (appliedAt)", defaultValue = "appliedAt")
    private String sortBy = "appliedAt";

    @Schema(description = "정렬 방향 (ASC, DESC)", defaultValue = "DESC")
    private String sortDirection = "DESC";
}
