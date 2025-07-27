package com.example.nexus.app.post.service;

import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.global.s3.S3UploadService;
import com.example.nexus.app.post.controller.dto.request.PostCreateRequest;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.RewardType;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.user.domain.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private S3UploadService s3UploadService;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("이미지와 함께 게시글 생성 성공")
    void createPost_withImage_success() {
        // given
        PostCreateRequest request = createPostRequest();
        MultipartFile mockFile = createMockFile();
        CustomUserDetails userDetails = createMockUser();

        // Mock 설정 - genreCategoryRepository는 호출되지 않으므로 stubbing 불필요
        given(s3UploadService.uploadFile(mockFile))
                .willReturn("https://test-bucket.s3.amazonaws.com/test-image.jpg");
        given(postRepository.save(any(Post.class)))
                .willAnswer(invocation -> {
                    Post savedPost = invocation.getArgument(0);
                    Post mockPost = spy(savedPost);
                    given(mockPost.getId()).willReturn(1L);
                    return mockPost;
                });

        // when
        Long postId = postService.createPost(request, mockFile, userDetails);

        // then
        assertThat(postId).isEqualTo(1L);
        verify(s3UploadService).uploadFile(mockFile);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("이미지 없이 게시글 생성 성공")
    void createPost_withoutImage_success() {
        // given
        PostCreateRequest request = createPostRequest();
        CustomUserDetails userDetails = createMockUser();

        given(postRepository.save(any(Post.class)))
                .willAnswer(invocation -> {
                    Post savedPost = invocation.getArgument(0);
                    Post mockPost = spy(savedPost);
                    given(mockPost.getId()).willReturn(1L);
                    return mockPost;
                });

        // when
        Long postId = postService.createPost(request, null, userDetails);

        // then
        assertThat(postId).isEqualTo(1L);
        verify(s3UploadService, never()).uploadFile(any());
        verify(postRepository).save(any(Post.class));
    }

    // createPostRequest() 메서드 수정
    private PostCreateRequest createPostRequest() {
        return new PostCreateRequest(
                "테스트 제목",
                "서비스 요약",
                "제작자 소개",
                "상세 설명",
                "피드백 방법",
                "30분",
                "온라인",
                "QNA",
                RewardType.GIFT_CARD,
                50,
                "ALL",
                18,
                65,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(30),
                MainCategory.APP,
                PlatformCategory.ANDROID,
                Set.of(GenreCategory.LIFESTYLE, GenreCategory.SOCIAL)
        );
    }

    private MultipartFile createMockFile() {
        return new MockMultipartFile(
                "thumbnail",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    private CustomUserDetails createMockUser() {
        return new CustomUserDetails(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                "test@test.com",
                RoleType.ROLE_USER,
                1L
        );
    }
}
