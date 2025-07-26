package com.example.nexus.app.post.domain;

import com.example.nexus.app.category.domain.GenreCategory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "post_genre_categories",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"post_id", "genre_category_id"})})
@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PostGenreCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_category_id", nullable = false)
    private GenreCategory genreCategory;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Builder
    public PostGenreCategory(Post post, GenreCategory genreCategory) {
        this.post = post;
        this.genreCategory = genreCategory;
    }
}