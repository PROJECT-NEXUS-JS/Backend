package com.example.nexus.app.badge.dto;

import com.example.nexus.app.badge.domain.Badge;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadgeStatisticsDto {
    private Badge badge;
    private Long count;
}


