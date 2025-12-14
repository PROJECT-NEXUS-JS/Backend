package com.example.nexus.app.post.controller.doc;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.post.controller.dto.request.PostCreateRequest;
import com.example.nexus.app.post.controller.dto.request.PostUpdateRequest;
import com.example.nexus.app.post.controller.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "게시글", description = "게시글 관련 API")
public interface PostControllerDoc {

    @Operation(
            summary = "게시글 생성",
            description = """
                   새로운 게시글을 생성합니다.
                   
                   **Request에 포함되는 Enum 타입들:**
                   **MainCategory (메인 카테고리):**
                    - `WEB`: 웹
                    - `APP`: 앱
                    - `GAME`: 게임
                    - `ETC`: 기타
                   
                   **PlatformCategory (플랫폼 카테고리):**
                    - 웹: `WEB_ALL`
                    - 앱: `ANDROID`, `IOS`, `APP_ALL`
                    - 게임: `PC`, `MOBILE`, `CONSOLE`, `VR`, `GAME_ALL`
                    - 기타: `ETC_ALL`
                   
                   **GenreCategory (장르 카테고리):**
                    - 앱: `LIFESTYLE`, `EDUCATION`, `SOCIAL`, `AI_EXPERIMENTAL`, `PRODUCTIVITY`,
                         `COMMERCE`, `HEALTH_FITNESS`, `ENTERTAINMENT`, `FINANCE`, `BUSINESS`, `MEDIA`
                    - 웹: `PRODUCTIVITY_COLLABORATION`, `COMMERCE_SHOPPING_WEB`, `MARKETING_PROMOTION`,
                         `COMMUNITY_SOCIAL_WEB`, `EDUCATION_CONTENT`, `FINANCE_ASSET`, `AI_AUTOMATION`, `EXPERIMENTAL_WEB`,
                         `LIFESTYLE_HOBBY`, `RECRUITMENT_HR`, `CRM_SALES`
                    - 게임: `CASUAL`, `PUZZLE_BOARD`, `RPG_ADVENTURE`, `SIMULATION_GAME`, `STRATEGY_CARD`, `SPORTS_RACING`, `MULTIPLAYER_SOCIAL`
                    - 기타: `ETC`
                   
                   **RewardType (리워드 타입):**
                    - `CASH`: 현금 지급
                    - `GIFT_CARD`: 기프티콘
                    - `PRODUCT`: 제품 지급
                    - `NONE`: 보상 없음
                   
                   **PrivacyItem (개인정보 수집 항목):**
                    - `NAME`: 이름
                    - `EMAIL`: 이메일
                    - `CONTACT`: 연락처
                    - `OTHER`: 기타
                   """
    )
    ResponseEntity<ApiResponse<Long>> createPost(
            @Valid @RequestPart("data") PostCreateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "임시 저장",
            description = """
                    게시글을 임시 저장합니다.
                    
                    게시글 생성 API와 동일한 Request 형식을 사용하며, 상태가 `DRAFT`로 저장됩니다.
                    """
    )
    ResponseEntity<ApiResponse<Long>> saveDraft(
            @Valid @RequestPart("data") PostCreateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "임시 게시글 수정 및 활성화",
            description = """
                    임시 저장된 게시글을 수정하고 바로 활성화합니다.
                    
                    게시글 상태가 `DRAFT`에서 `ACTIVE`로 변경됩니다.
                    """
    )
    ResponseEntity<ApiResponse<Void>> updateAndPublishDraft(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @Valid @RequestPart("data") PostUpdateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "임시 게시글 활성화",
            description = """
                    임시 저장된 게시글을 수정 없이 바로 활성화합니다.
                    
                    게시글 상태가 `DRAFT`에서 `ACTIVE`로 변경됩니다.
                    """
    )
    ResponseEntity<ApiResponse<Void>> publishPost(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "게시글 상세 조회",
            description = "게시글 ID로 상세 정보를 조회합니다."
    )
    ResponseEntity<ApiResponse<PostDetailResponse>> getPost(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "게시글 목록 조회 (필터 검색 가능)",
            description = """
                  조건에 따라 게시글을 조회합니다.

                  **MainCategory (메인 카테고리):**
                  - `WEB`: 웹
                  - `APP`: 앱
                  - `GAME`: 게임
                  - `ETC`: 기타

                  **PlatformCategory (플랫폼 카테고리):**
                  - `ANDROID`, `IOS`, `PC`, `MOBILE`, `CONSOLE`, `VR` 등

                  **GenreCategory (장르 카테고리):**
                  - `LIFESTYLE`, `EDUCATION`, `SOCIAL`, `PRODUCTIVITY`, `MARKETING_PROMOTION` 등

                  **정렬 기준 (sortBy):**
                  - `latest`: 최신순
                  - `popular`: 인기순
                  - `deadline`: 마감임박순
                  - `viewCount`: 조회수순

                  **남은 일수 필터 (daysRemaining):**
                  - 해당 일수 이내에 마감되는 게시글만 조회
                  - 예: `daysRemaining=7`이면 7일 이내 마감 게시글만 조회
                  """
    )
    ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getPosts(
            @Parameter(description = "메인 카테고리 (WEB, APP, GAME, ETC)")
            @RequestParam(required = false) String mainCategory,
            @Parameter(description = "플랫폼 카테고리 (ANDROID, IOS, PC 등)")
            @RequestParam(required = false) String platformCategory,
            @Parameter(description = "장르 카테고리 (LIFESTYLE, EDUCATION, SOCIAL 등)")
            @RequestParam(required = false) String genreCategory,
            @Parameter(description = "검색 키워드")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "정렬 기준 (latest, popular, deadline, viewCount)")
            @RequestParam(defaultValue = "latest") String sortBy,
            @Parameter(description = "남은 일수 (해당 일수 이내 마감 게시글만 조회)", example = "7")
            @RequestParam(required = false) Integer daysRemaining,
            Pageable pageable
    );

    @Operation(
            summary = "내 임시 저장 게시글 조회",
            description = """
                    사용자의 임시 저장된 게시글을 조회합니다.
                    
                    상태가 `DRAFT`인 게시글만 조회됩니다.
                    """
    )
    ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getMyDrafts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    );

    @Operation(
            summary = "내 게시글 조회",
            description = """
                    사용자가 작성한 활성 게시글을 조회합니다.
                    
                    상태가 `ACTIVE`인 게시글만 조회됩니다.
                    """
    )
    ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    );

    @Operation(
            summary = "게시글 수정",
            description = "게시글 정보를 수정합니다. 작성자만 수정 가능합니다."
    )
    ResponseEntity<ApiResponse<Void>> updatePost(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @Valid @RequestPart("data") PostUpdateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "게시글 삭제",
            description = "게시글을 삭제합니다. 작성자만 삭제 가능합니다."
    )
    ResponseEntity<ApiResponse<Void>> deletePost(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "게시글 상세 조회 (메인 뷰)",
            description = "게시글의 좌측 메인 뷰에 필요한 상세 정보를 조회합니다."
    )
    ResponseEntity<ApiResponse<PostMainViewDetailResponse>> getPostMainView(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId
    );

    @Operation(
            summary = "유사 게시글 목록 조회",
            description = "특정 게시글과 유사한 게시글 목록을 조회합니다."
    )
    ResponseEntity<ApiResponse<List<SimilarPostResponse>>> getSimilarPosts(
            @Parameter(description = "기준 게시글 ID", required = true)
            @PathVariable Long postId,
            @Parameter(description = "가져올 유사 게시글 최대 개수", example = "3")
            @RequestParam(defaultValue = "3") int limit
    );

    @Operation(
            summary = "게시글 상세 조회 (우측 사이드바)",
            description = "게시글의 우측 사이드바에 필요한 상세 정보를 조회합니다."
    )
    ResponseEntity<ApiResponse<PostRightSidebarResponse>> getPostRightSidebar(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId
    );
}
