package com.example.nexus.app.mypage.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WatchlistDto {
    private List<TestDeadlineDto> testsNearingDeadline;
}
