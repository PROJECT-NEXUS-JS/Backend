package com.example.nexus.app.post.service.dto;

import com.example.nexus.app.participation.domain.ParticipationStatus;

public record PostUserStatus(
        Boolean isLiked,
        Boolean isParticipated,
        ParticipationStatus participationStatus
) {
    public static PostUserStatus empty() {
        return new PostUserStatus(false, false, null);
    }

    public static PostUserStatus liked() {
        return new PostUserStatus(true, null, null);
    }

    public static PostUserStatus participated() {
        return new PostUserStatus(null, true, null);
    }
}
