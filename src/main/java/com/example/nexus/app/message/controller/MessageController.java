package com.example.nexus.app.message.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
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

@Tag(name = "메시지", description = "메시지 관련 API")
@RestController
@RequestMapping("/v1/users/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "내 채팅방 목록", description = "사용자의 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<MessageRoomResponse>>> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MessageRoomResponse> response = messageService.findMyRooms(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "채팅방 생성", description = "게시글에 대한 새로운 채팅방을 생성합니다.")
    @PostMapping("/rooms/create")
    public ResponseEntity<ApiResponse<MessageRoomResponse>> createRoom(
            @Parameter(description = "게시글 ID", required = true) @RequestParam Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageRoomResponse response = messageService.createRoom(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "채팅방 조회", description = "게시글에 대한 기존 채팅방을 조회합니다.")
    @GetMapping("/rooms/find")
    public ResponseEntity<ApiResponse<MessageRoomResponse>> findRoom(
            @Parameter(description = "게시글 ID", required = true) @RequestParam Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageRoomResponse response = messageService.findRoom(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "채팅방 메시지 조회", description = "특정 채팅방의 메시지들을 조회합니다. (조회 후 읽음 처리 해야 함)")
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getRoomMessages(
            @Parameter(description = "채팅방 ID", required = true) @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<MessageResponse> response = messageService.findRoomMessages(roomId, userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "텍스트 메시지 전송", description = "채팅방에 텍스트 메시지를 전송합니다.")
    @PostMapping("/rooms/{roomId}/send")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @Parameter(description = "채팅방 ID", required = true) @PathVariable Long roomId,
            @RequestBody MessageSendRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageResponse response = messageService.sendMessage(roomId, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "파일 첨부 메시지 전송", description = "채팅방에 파일을 첨부하여 메시지를 전송합니다.")
    @PostMapping(value = "/rooms/{roomId}/send-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MessageResponse>> sendFileMessage(
            @Parameter(description = "채팅방 ID", required = true) @PathVariable Long roomId,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "message", required = false) String message,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageResponse response = messageService.sendFileMessage(roomId, file, message, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "메시지 읽음 처리", description = "채팅방의 안 읽은 메시지를 모두 읽음 처리합니다.")
    @PatchMapping("/rooms/{roomId}/read")
    public ResponseEntity<ApiResponse<Void>> markMessagesAsRead(
            @Parameter(description = "채팅방 ID", required = true) @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        messageService.markMessagesAsRead(roomId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "안 읽은 메시지 개수", description = "사용자의 전체 안 읽은 메시지 개수를 조회합니다.")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Integer>> getUnreadMessageCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer unreadCount = messageService.getUnreadMessageCount(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(unreadCount));
    }

    @Operation(summary = "채팅방 별 안 읽은 메시지 개수", description = "특정 채팅방의 안 읽은 메시지 개수를 조회합니다.")
    @GetMapping("/rooms/{roomId}/unread-count")
    public ResponseEntity<ApiResponse<Integer>> getRoomUnreadCount(
            @Parameter(description = "채팅방 ID", required = true) @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer unreadCount = messageService.getRoomUnreadCount(roomId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(unreadCount));
    }
}
