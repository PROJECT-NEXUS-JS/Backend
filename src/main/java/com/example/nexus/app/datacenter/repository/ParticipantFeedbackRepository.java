package com.example.nexus.app.datacenter.repository;

import com.example.nexus.app.datacenter.domain.ParticipantFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantFeedbackRepository extends JpaRepository<ParticipantFeedback, Long>, ParticipantFeedbackRepositoryCustom {

    Optional<ParticipantFeedback> findByParticipationId(Long participationId);

    List<ParticipantFeedback> findByPostId(Long postId);
}

