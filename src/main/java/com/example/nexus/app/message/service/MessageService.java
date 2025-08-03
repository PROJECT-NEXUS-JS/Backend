package com.example.nexus.app.message.service;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.s3.S3UploadService;
import com.example.nexus.app.message.controller.dto.request.MessageSendRequest;
import com.example.nexus.app.message.controller.dto.response.MessageResponse;
import com.example.nexus.app.message.controller.dto.response.MessageRoomResponse;
import com.example.nexus.app.message.controller.dto.response.TodayMessageResponse;
import com.example.nexus.app.message.domain.Message;
import com.example.nexus.app.message.domain.MessageRoom;
import com.example.nexus.app.message.domain.MessageType;
import com.example.nexus.app.message.repository.MessageRepository;
import com.example.nexus.app.message.repository.MessageRoomRepository;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageRoomRepository messageRoomRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    public List<MessageRoomResponse> findMyRooms(Long userId) {
        List<MessageRoom> rooms = messageRoomRepository.findByUserIdOrderByLastMessageDesc(userId);

        return rooms.stream()
                .map(room -> MessageRoomResponse.from(room, userId))
                .toList();
    }

    public MessageRoomResponse findRoom(Long postId, Long userId) {
        Post post = getPost(postId);
        User participant = getUser(userId);
        User postOwner = getUser(post.getCreatedBy());

        MessageRoom room = messageRoomRepository.findByPostAndUsers(postId, postOwner.getId(),
                        participant.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_ROOM_NOT_FOUND));

        return MessageRoomResponse.from(room, userId);
    }

    public Page<MessageResponse> findRoomMessages(Long roomId, Long userId, Pageable pageable) {
        MessageRoom room = findRoomByIdAndUserId(roomId, userId);
        Page<Message> messages = messageRepository.findByRoomIdAndNotDeleted(roomId, pageable);

        return messages.map(message -> MessageResponse.from(message, userId));
    }

    @Transactional
    public MessageResponse sendMessage(Long roomId, MessageSendRequest request, Long userId) {
        MessageRoom room = findRoomByIdAndUserId(roomId, userId);
        User sender = getUser(userId);

        Message message = Message.createTextMessage(room, sender, request.content());
        Message savedMessage = messageRepository.save(message);

        updateRoomAfterMessage(room, savedMessage);

        return MessageResponse.from(savedMessage, userId);
    }

    @Transactional
    public MessageResponse sendFileMessage(Long roomId, MultipartFile file, String message, Long userId) {
        MessageRoom room = findRoomByIdAndUserId(roomId, userId);
        User sender = getUser(userId);

        String fileUrl = s3UploadService.uploadFile(file);
        String fileName = file.getOriginalFilename();
        Long fileSize = file.getSize();
        MessageType messageType = determineFileType(file);

        String content = message != null ? message : fileName;

        Message fileMessage = Message.createFileMessage(room, sender, content, fileUrl,
                fileName, fileSize, messageType);
        Message savedMessage = messageRepository.save(fileMessage);
        updateRoomAfterMessage(room, savedMessage);

        return MessageResponse.from(savedMessage, userId);
    }

    @Transactional
    public MessageRoomResponse createRoom(Long postId, Long userId) {
        Post post = getPost(postId);
        User participant = getUser(userId);
        User postOwner = getUser(post.getCreatedBy());

        Optional<MessageRoom> existingRoom = messageRoomRepository.findByPostAndUsers(postId, postOwner.getId(), participant.getId());

        if (existingRoom.isPresent()) {
            throw new GeneralException(ErrorStatus.MESSAGE_ROOM_ALREADY_EXISTS);
        }

        MessageRoom newRoom = MessageRoom.create(post, postOwner, participant);
        MessageRoom savedRoom = messageRoomRepository.save(newRoom);

        return MessageRoomResponse.from(savedRoom, userId);
    }

    @Transactional
    public void markMessagesAsRead(Long roomId, Long userId) {
        MessageRoom room = findRoomByIdAndUserId(roomId, userId);

        messageRepository.markMessagesAsReadByRoom(roomId, userId, LocalDateTime.now());
        
        long unreadMessages = messageRepository.countUnreadMessagesByRoom(roomId, userId);
        if (unreadMessages == 0) {
            room.resetUnreadCount(userId);
        }
    }

    public Integer getUnreadMessageCount(Long userId) {
        return messageRoomRepository.getTotalUnreadCount(userId);
    }

    public Integer getRoomUnreadCount(Long roomId, Long userId) {
        MessageRoom room = findRoomByIdAndUserId(roomId, userId);
        return room.getUnreadCountForUser(userId);
    }

    public List<TodayMessageResponse> getTodayMessages(Long postId, Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<Message> messages = messageRepository.findTodayMessagesByPost(postId, userId, startOfDay, endOfDay);

        return messages.stream()
                .limit(2)
                .map(this::createTodayMessageResponse)
                .toList();
    }

    public Integer getUnreadMessageCountByPost(Long postId, Long userId) {
        return messageRoomRepository.getUnreadCountByPost(postId, userId);
    }

    private MessageRoom findRoomByIdAndUserId(Long roomId, Long userId) {
        return messageRoomRepository.findByIdAndUserId(roomId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_ROOM_NOT_FOUND));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private void updateRoomAfterMessage(MessageRoom room, Message message) {
        room.updateLastMessage(message.getContent(), message.getCreatedAt());
        room.incrementUnreadCount(message.getSender().getId());
    }

    private MessageType determineFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            return MessageType.IMAGE;
        }
        return MessageType.FILE;
    }

    private TodayMessageResponse createTodayMessageResponse(Message message) {
        return new TodayMessageResponse(
                message.getId(),
                message.getSender().getNickname(),
                getMessagePreview(message),
                message.getCreatedAt(),
                message.getIsRead()
        );
    }

    private String getMessagePreview(Message message) {
        if (message.isFileMessage()) {
            return "[" + message.getMessageType().getDescription() + "] " +
                    message.getFileName();
        }

        String content = message.getContent();
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }
}
