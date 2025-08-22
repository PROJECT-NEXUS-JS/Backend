package com.example.nexus.app.mypage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {
    private String profileImageUrl;
    private String name;
    private String affiliation;
    private int testsUploaded;
    private int testsParticipating;
}
