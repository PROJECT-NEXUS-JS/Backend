package com.example.nexus.app.participation.repository;

import com.example.nexus.app.participation.domain.Participation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParticipationRepositoryCustom {
    Page<Participation> findParticipantsWithFilters(Long postId, String status,
                                                    String searchKeyword, Pageable pageable);
}
