package com.example.nexus.app.message.repository;

import com.example.nexus.app.message.domain.MessageRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {

    @Query("SELECT mr FROM MessageRoom mr " +
            "WHERE (mr.postOwner.id = :userId OR mr.participant.id = :userId) " +
            "ORDER BY mr.lastMessageAt DESC NULLS LAST, mr.createdAt DESC")
    List<MessageRoom> findByUserIdOrderByLastMessageDesc(@Param("userId") Long userId);

    @Query("SELECT mr FROM MessageRoom mr " +
            "WHERE mr.post.id = :postId " +
            "AND ((mr.postOwner.id = :postOwnerId AND mr.participant.id = :participantId) " +
            "OR (mr.postOwner.id = :participantId AND mr.participant.id = :postOwnerId))")
    Optional<MessageRoom> findByPostAndUsers(@Param("postId") Long postId,
                                             @Param("postOwnerId") Long postOwnerId,
                                             @Param("participantId") Long participantId);

    @Query("SELECT mr FROM MessageRoom mr " +
            "WHERE mr.id = :roomId " +
            "AND (mr.postOwner.id = :userId OR mr.participant.id = :userId)")
    Optional<MessageRoom> findByIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(CASE " +
            "WHEN mr.postOwner.id = :userId THEN mr.unreadCountOwner " +
            "WHEN mr.participant.id = :userId THEN mr.unreadCountParticipant " +
            "ELSE 0 END), 0) " +
            "FROM MessageRoom mr " +
            "WHERE (mr.postOwner.id = :userId OR mr.participant.id = :userId)")
    Integer getTotalUnreadCount(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(mr.unreadCountOwner), 0) " +
            "FROM MessageRoom mr " +
            "WHERE mr.post.id = :postId " +
            "AND mr.postOwner.id = :userId")
    Integer getUnreadCountByPost(@Param("postId") Long postId, @Param("userId") Long userId);
}
