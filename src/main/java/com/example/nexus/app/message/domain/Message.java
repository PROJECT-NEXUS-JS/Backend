package com.example.nexus.app.message.domain;

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
@Table(name = "messages")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private MessageRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "message_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

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

    public static Message createTextMessage(MessageRoom room, User sender, String content) {
        Message message = new Message();
        message.room = room;
        message.sender = sender;
        message.content = content;
        message.messageType = MessageType.TEXT;
        return message;
    }

    public static Message createFileMessage(MessageRoom room, User sender, String content,
                                            String fileUrl, String fileName, Long fileSize,
                                            MessageType messageType) {
        Message message = new Message();
        message.room = room;
        message.sender = sender;
        message.content = content;
        message.messageType = messageType;
        message.fileUrl = fileUrl;
        message.fileName = fileName;
        message.fileSize = fileSize;
        return message;
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
    }

    public boolean isFileMessage() {
        return messageType == MessageType.FILE || messageType == MessageType.IMAGE;
    }

    public boolean canBeReadBy(Long userId) {
        return !sender.getId().equals(userId);
    }
}
