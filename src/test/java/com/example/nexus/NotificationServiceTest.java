package com.example.nexus;

import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import com.example.nexus.notification.NotificationType;
import com.example.nexus.notification.domain.Notification;
import com.example.nexus.notification.repository.EmitterRepository;
import com.example.nexus.notification.repository.NotificationRepository;
import com.example.nexus.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmitterRepository emitterRepository;

    private User testUser;
    private final Long TEST_USER_ID = 1L;
    private final Long TEST_NOTIFICATION_ID = 100L;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        User mockUser = mock(User.class);
        lenient().when(mockUser.getId()).thenReturn(TEST_USER_ID);
        lenient().when(mockUser.getEmail()).thenReturn("test@user.com");
        lenient().when(mockUser.getNickname()).thenReturn("testUser");

        this.testUser = mockUser;

        testNotification = mock(Notification.class);
        lenient().when(testNotification.getId()).thenReturn(TEST_NOTIFICATION_ID);
        lenient().when(testNotification.getUser()).thenReturn(testUser);
        lenient().when(testNotification.getType()).thenReturn(NotificationType.MESSAGE);
        lenient().when(testNotification.getContent()).thenReturn("Test Content");
        lenient().when(testNotification.getLink()).thenReturn("/test");
        lenient().when(testNotification.isRead()).thenReturn(false);
        lenient().when(testNotification.getCreatedAt()).thenReturn(LocalDateTime.now());
    }

    private void mockFindUserById() {
        doReturn(Optional.of(testUser)).when(userRepository).findById(TEST_USER_ID);
    }


    @Test
    @DisplayName("SSE 구독 테스트: Emitter가 생성되고 저장되어야 한다")
    void subscribe_success() {
        SseEmitter mockEmitter = mock(SseEmitter.class);
        doReturn(mockEmitter).when(emitterRepository).save(eq(TEST_USER_ID), any(SseEmitter.class));

        SseEmitter resultEmitter = notificationService.subscribe(TEST_USER_ID);

        assertNotNull(resultEmitter);
        verify(emitterRepository, times(1)).save(eq(TEST_USER_ID), any(SseEmitter.class));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("알림 생성 및 SSE 전송 테스트: DB 저장 후 SSE 푸시가 성공해야 한다")
    void createNotification_success_and_send() {
        mockFindUserById();

        Notification savedNotification = mock(Notification.class);
        when(savedNotification.getId()).thenReturn(TEST_NOTIFICATION_ID);
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        SseEmitter mockEmitter = mock(SseEmitter.class);
        doReturn(mockEmitter).when(emitterRepository).findById(TEST_USER_ID);

        notificationService.createNotification(TEST_USER_ID, NotificationType.NEW_REVIEW, "새로운 리뷰 알림", "/post/1");

        verify(notificationRepository, times(1)).save(any(Notification.class));

        verify(emitterRepository, times(1)).findById(TEST_USER_ID);

        try {
            verify(mockEmitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
        } catch (Exception e) {
            fail("SSE send should not throw exception.");
        }
    }

    @Test
    @DisplayName("알림 생성 시 Emitter가 없으면 DB 저장만 되어야 한다")
    void createNotification_no_emitter() {
        mockFindUserById();
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        doReturn(null).when(emitterRepository).findById(TEST_USER_ID);

        notificationService.createNotification(TEST_USER_ID, NotificationType.NEW_REVIEW, "댓글 알림", "/post/1");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("SSE 전송 중 예외 발생 시 Emitter가 삭제되어야 한다")
    void sendNotification_exception_should_delete_emitter() throws Exception {
        SseEmitter mockEmitter = mock(SseEmitter.class);
        doReturn(mockEmitter).when(emitterRepository).findById(TEST_USER_ID);

        doThrow(new RuntimeException("Connection Broken")).when(mockEmitter).send(any(SseEmitter.SseEventBuilder.class));

        mockFindUserById();

        Notification savedNotification = mock(Notification.class);
        when(savedNotification.getId()).thenReturn(TEST_NOTIFICATION_ID);
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        notificationService.createNotification(TEST_USER_ID, NotificationType.NEW_REVIEW, "댓글 알림", "/post/1");

        verify(emitterRepository, times(1)).deleteById(TEST_USER_ID);
    }
}
