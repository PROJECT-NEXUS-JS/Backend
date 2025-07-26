package com.example.nexus.app.category.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "genre_categories")
@Entity
@Getter
@NoArgsConstructor
public class GenreCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String code;
    
    @Builder
    public GenreCategory(String name, String code) {
        this.name = name;
        this.code = code;
    }
    
    public void updateInfo(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
