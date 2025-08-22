package com.example.nexus.app.mypage.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RecentlyViewedTestDto {
    private Long postId;
    private String category;
    private String title;
    private String oneLineIntro;
    private List<String> tags;
    private LocalDateTime viewedAt;
}
