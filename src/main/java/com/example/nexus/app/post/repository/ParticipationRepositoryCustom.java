package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.Participation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParticipationRepositoryCustom {
    Page<Participation> findParticipantsWithFilters(Long postId, String status, String rewardStatus,
                                                    String nickname, String sortBy, String sortDirection, Pageable pageable);
}
