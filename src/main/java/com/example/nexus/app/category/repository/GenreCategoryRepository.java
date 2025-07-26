package com.example.nexus.app.category.repository;

import com.example.nexus.app.category.domain.GenreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreCategoryRepository extends JpaRepository<GenreCategory, Long> {
    
    List<GenreCategory> findByIdIn(List<Long> ids);
    
    List<GenreCategory> findAllByOrderByNameAsc();
}
