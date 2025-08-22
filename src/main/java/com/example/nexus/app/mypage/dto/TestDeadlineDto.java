package com.example.nexus.app.mypage.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestDeadlineDto {
    private Long postId;
    private String category;
    private String title;
    private List<String> tags;
    private LocalDate deadline;
}
