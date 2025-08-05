package com.example.nexus.app.message.domain;

import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_rooms", uniqueConstraints =
@UniqueConstraint(columnNames = {"post_id", "post_owner_id", "participant_id"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class MessageRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_owner_id", nullable = false)
    private User postOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "unread_count_owner", nullable = false)
    private Integer unreadCountOwner = 0;

    @Column(name = "unread_count_participant", nullable = false)
    private Integer unreadCountParticipant = 0;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    public static MessageRoom create(Post post, User postOwner, User participant) {
        MessageRoom room = new MessageRoom();
        room.post = post;
        room.postOwner = postOwner;
        room.participant = participant;
        return room;
    }

    public void updateLastMessage(String content, LocalDateTime sentAt) {
        this.lastMessage = content;
        this.lastMessageAt = sentAt;
    }

    public void incrementUnreadCount(Long senderId) {
        if (senderId.equals(participant.getId())) {
            this.unreadCountOwner++;
        } else if (senderId.equals(postOwner.getId())) {
            this.unreadCountParticipant++;
        }
    }

    public void resetUnreadCount(Long userId) {
        if (userId.equals(postOwner.getId())) {
            this.unreadCountOwner = 0;
        } else if (userId.equals(participant.getId())) {
            this.unreadCountParticipant = 0;
        }
    }

    public Integer getUnreadCountForUser(Long userId) {
        if (userId.equals(postOwner.getId())) {
            return unreadCountOwner;
        } else if (userId.equals(participant.getId())) {
            return unreadCountParticipant;
        }
        return 0;
    }

    public User getOtherUser(Long currentUserId) {
        return currentUserId.equals(postOwner.getId()) ? participant : postOwner;
    }

}
