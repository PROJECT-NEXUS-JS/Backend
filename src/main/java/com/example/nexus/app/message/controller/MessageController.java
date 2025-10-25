package com.example.nexus.app.message.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.message.controller.doc.MessageControllerDoc;
import com.example.nexus.app.message.controller.dto.request.MessageSendRequest;
import com.example.nexus.app.message.controller.dto.response.MessageResponse;
import com.example.nexus.app.message.controller.dto.response.MessageRoomResponse;
import com.example.nexus.app.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/users/messages")
@RequiredArgsConstructor
public class MessageController implements MessageControllerDoc {

    private final MessageService messageService;

    @Override
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<MessageRoomResponse>>> getMyRooms(
            @RequestParam(value = "unreadOnly", required = false, defaultValue = "false") Boolean unreadOnly,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MessageRoomResponse> response = messageService.findMyRooms(userDetails.getUserId(), unreadOnly);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @PostMapping("/rooms/create")
    public ResponseEntity<ApiResponse<MessageRoomResponse>> createRoom(
            @RequestParam Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageRoomResponse response = messageService.createRoom(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/rooms/find")
    public ResponseEntity<ApiResponse<MessageRoomResponse>> findRoom(
            @RequestParam Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageRoomResponse response = messageService.findRoom(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getRoomMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<MessageResponse> response = messageService.findRoomMessages(roomId, userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @PostMapping("/rooms/{roomId}/send")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @PathVariable Long roomId,
            @RequestBody MessageSendRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageResponse response = messageService.sendMessage(roomId, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @PostMapping(value = "/rooms/{roomId}/send-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MessageResponse>> sendFileMessage(
            @PathVariable Long roomId,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "message", required = false) String message,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageResponse response = messageService.sendFileMessage(roomId, file, message, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Integer>> getUnreadMessageCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer unreadCount = messageService.getUnreadMessageCount(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(unreadCount));
    }

    @Override
    @GetMapping("/rooms/{roomId}/unread-count")
    public ResponseEntity<ApiResponse<Integer>> getRoomUnreadCount(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer unreadCount = messageService.getRoomUnreadCount(roomId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(unreadCount));
    }
}
