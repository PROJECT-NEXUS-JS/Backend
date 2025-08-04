package com.example.nexus.app.recruitment.service;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.global.s3.S3UploadService;
import com.example.nexus.app.recruitment.controller.dto.request.RecruitmentProfileUpdateRequest;
import com.example.nexus.app.recruitment.controller.dto.response.RecruitmentProfileResponse;
import com.example.nexus.app.recruitment.domain.RecruitmentProfile;
import com.example.nexus.app.recruitment.repository.RecruitmentProfileRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RecruitmentProfileService {

    private final UserRepository userRepository;
    private final RecruitmentProfileRepository recruitmentProfileRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public RecruitmentProfileResponse getMyProfile(Long userId) {
        User user = getUser(userId);
        RecruitmentProfile profile = getOrCreateProfile(user);
        return RecruitmentProfileResponse.from(user, profile);
    }

    @Transactional
    public RecruitmentProfileResponse updateCompleteProfile(Long userId, RecruitmentProfileUpdateRequest request, MultipartFile image) {
        User user = getUser(userId);
        RecruitmentProfile profile = getOrCreateProfile(user);

        String newNickname = request.nickname() != null && !request.nickname().trim().isEmpty()
                ? request.nickname()
                : user.getNickname();
        String newIntroduction = request.introduction() != null
                ? request.introduction()
                : (profile.getIntroduction() != null ? profile.getIntroduction() : "");
        user.updateNickname(newNickname);

        profile.updateIntroduction(newIntroduction);

        if (image != null && !image.isEmpty()) {
            validateImageFile(image);

            if (user.getProfileUrl() != null &&
                    !isDefaultProfileImage(user.getProfileUrl())) {
                try {
                    s3UploadService.deleteFile(user.getProfileUrl());
                } catch (Exception e) {
                    log.warn("이미지 삭제 실패: {}", e.getMessage());
                }
            }

            String imageUrl = s3UploadService.uploadFile(image);
            user.updateProfileImage(imageUrl);
        }

        return RecruitmentProfileResponse.from(user, profile);
    }

    private RecruitmentProfile getOrCreateProfile(User user) {
        return recruitmentProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    try {
                        RecruitmentProfile newProfile = RecruitmentProfile.create(user, "");
                        return recruitmentProfileRepository.save(newProfile);
                    } catch (DataIntegrityViolationException e) {
                        return recruitmentProfileRepository.findByUser(user)
                                .orElseThrow(() -> new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR));
                    }
                });
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private void validateImageFile(MultipartFile image) {
        // 파일 크기 체크 (5MB)
        if (image.getSize() > 5 * 1024 * 1024) {
            throw new GeneralException(ErrorStatus.IMAGE_SIZE_EXCEEDED);
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new GeneralException(ErrorStatus.INVALID_IMAGE_FORMAT);
        }

        // 지원하는 이미지 형식 체크
        String[] allowedTypes = {"image/jpeg", "image/png"};
        if (!Arrays.asList(allowedTypes).contains(contentType.toLowerCase())) {
            throw new GeneralException(ErrorStatus.INVALID_IMAGE_FORMAT);
        }
    }

    private boolean isDefaultProfileImage(String profileUrl) {
        return profileUrl == null || profileUrl.contains("default-profile") || profileUrl.contains("kakao");
    }
}
