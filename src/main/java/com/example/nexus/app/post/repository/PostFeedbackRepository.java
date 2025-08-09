package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.PostFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostFeedbackRepository extends JpaRepository<PostFeedback, Long> {

    Optional<PostFeedback> findByPostId(Long postId);

    List<PostFeedback> findByFeedbackMethod(String feedbackMethod);
}
