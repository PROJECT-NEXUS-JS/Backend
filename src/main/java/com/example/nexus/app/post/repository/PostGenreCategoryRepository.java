package com.example.nexus.app.post.repository;

import com.example.nexus.app.post.domain.PostGenreCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostGenreCategoryRepository extends JpaRepository<PostGenreCategory, Long> {
    
    List<PostGenreCategory> findByPostIdOrderByCreatedAt(Long postId);
    
    @Query("SELECT pgc FROM PostGenreCategory pgc JOIN FETCH pgc.genreCategory WHERE pgc.post.id = :postId ORDER BY pgc.createdAt")
    List<PostGenreCategory> findByPostIdWithGenreCategory(@Param("postId") Long postId);
    
    void deleteByPostIdAndGenreCategoryId(Long postId, Long genreCategoryId);
    
    boolean existsByPostIdAndGenreCategoryId(Long postId, Long genreCategoryId);
    
    List<PostGenreCategory> findByPostId(Long postId);
}