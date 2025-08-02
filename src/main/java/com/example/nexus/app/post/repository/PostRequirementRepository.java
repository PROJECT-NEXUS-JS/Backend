package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.PostRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRequirementRepository extends JpaRepository<PostRequirement, Long> {

    Optional<PostRequirement> findByPostId(Long postId);

    @Query("SELECT pr " +
            "FROM PostRequirement pr " +
            "WHERE pr.maxParticipants IS NOT NULL AND pr.maxParticipants <= :limit")
    List<PostRequirement> findByMaxParticipantsLessThanEqual(@Param("limit") Integer limit);

    @Query("SELECT pr " +
            "FROM PostRequirement pr " +
            "WHERE pr.ageMin <= :age AND pr.ageMax >= :age")
    List<PostRequirement> findByAgeRange(@Param("age") Integer age);
}
