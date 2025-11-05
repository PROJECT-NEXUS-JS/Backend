package com.example.nexus.app.message.repository;

import com.example.nexus.app.message.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @EntityGraph(attributePaths = {"sender", "room"})
    @Query(value = "SELECT m FROM Message m " +
            "WHERE m.room.id = :roomId AND m.isDeleted = false " +
            "ORDER BY m.createdAt ASC", countQuery = "SELECT COUNT(m) FROM Message m " +
            "WHERE m.room.id = :roomId AND m.isDeleted = false")
    Page<Message> findByRoomIdAndNotDeleted(@Param("roomId") Long roomId, Pageable pageable);

    @Modifying
    @Query("UPDATE Message m " +
            "SET m.isRead = true, m.readAt = :readAt " +
            "WHERE m.room.id = :roomId AND m.sender.id != :userId AND m.isRead = false " +
            "AND m.isDeleted = false")
    void markMessagesAsReadByRoom(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    @EntityGraph(attributePaths = {"sender", "room"})
    @Query("SELECT m FROM Message m " +
            "WHERE m.room.post.id = :postId " +
            "AND m.room.postOwner.id = :userId " +
            "AND m.sender.id != :userId " +
            "AND m.createdAt >= :startOfDay AND m.createdAt < :endOfDay " +
            "AND m.isDeleted = false " +
            "ORDER BY m.createdAt DESC")
    List<Message> findTodayMessagesByPost(@Param("postId") Long postId, @Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay,
                                          @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(m) FROM Message m " +
            "WHERE m.room.id = :roomId " +
            "AND m.sender.id != :userId " +
            "AND m.isRead = false " +
            "AND m.isDeleted = false")
    long countUnreadMessagesByRoom(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Query("SELECT m FROM Message m " +
            "JOIN FETCH m.sender " +
            "WHERE m.room.post.id = :postId " +
            "ORDER BY m.createdAt DESC")
    Page<Message> findByRoomPostId(Long postId, Pageable pageable);

    @Query("SELECT SUM(unread.count) FROM (" +
            "SELECT COUNT(m) as count FROM Message m " +
            "WHERE m.room.post.id = :postId " +
            "AND m.sender.id != :userId " +
            "AND m.isRead = false " +
            "AND m.isDeleted = false " +
            "GROUP BY m.room.id) unread")
    Long countUnreadMessagesByPostId(@Param("postId") Long postId, @Param("userId") Long userId);
}
