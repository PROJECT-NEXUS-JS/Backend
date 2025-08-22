package com.example.nexus.app.mypage.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class TotalParticipationDto {
    private int totalCount;
    private Map<String, Integer> countByCategory;
}
