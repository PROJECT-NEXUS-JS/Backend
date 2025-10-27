package com.example.nexus.app.message.controller.doc;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.message.controller.dto.request.MessageSendRequest;
import com.example.nexus.app.message.controller.dto.response.MessageResponse;
import com.example.nexus.app.message.controller.dto.response.MessageRoomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "메시지", description = "메시지 관련 API")
public interface MessageControllerDoc {

    @Operation(
            summary = "내 채팅방 목록 조회",
            description = "내가 참여한 채팅방 목록을 조회합니다."
    )
    ResponseEntity<ApiResponse<List<MessageRoomResponse>>> getMyRooms(
            @Parameter(name = "unreadOnly", required = false, description = "안 읽은 메시지가 있는 채팅방만 조회 (기본값: false)")
            @RequestParam Boolean unreadOnly,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "채팅방 생성",
            description = "게시글에 대한 새로운 채팅방을 생성합니다."
    )
    ResponseEntity<ApiResponse<MessageRoomResponse>> createRoom(
            @Parameter(description = "게시글 ID", required = true)
            @RequestParam Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "채팅방 조회",
            description = "게시글에 대한 기존 채팅방을 조회합니다."
    )
    ResponseEntity<ApiResponse<MessageRoomResponse>> findRoom(
            @Parameter(description = "게시글 ID", required = true)
            @RequestParam Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "채팅방 메시지 조회",
            description = "특정 채팅방의 메시지들을 조회합니다. (자동으로 읽음 처리됨)"
    )
    ResponseEntity<ApiResponse<Page<MessageResponse>>> getRoomMessages(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    );

    @Operation(
            summary = "텍스트 메시지 전송",
            description = "채팅방에 텍스트 메시지를 전송합니다."
    )
    ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long roomId,
            @RequestBody MessageSendRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "파일 첨부 메시지 전송",
            description = "채팅방에 파일을 첨부하여 메시지를 전송합니다."
    )
    ResponseEntity<ApiResponse<MessageResponse>> sendFileMessage(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long roomId,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "message", required = false) String message,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "안 읽은 메시지 개수",
            description = "사용자의 전체 안 읽은 메시지 개수를 조회합니다."
    )
    ResponseEntity<ApiResponse<Integer>> getUnreadMessageCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "채팅방 별 안 읽은 메시지 개수",
            description = "특정 채팅방의 안 읽은 메시지 개수를 조회합니다."
    )
    ResponseEntity<ApiResponse<Integer>> getRoomUnreadCount(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
