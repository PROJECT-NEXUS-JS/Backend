package com.example.nexus.app.user.service;

import com.example.nexus.app.category.domain.GenreCategory;
import com.example.nexus.app.category.domain.MainCategory;
import com.example.nexus.app.category.domain.PlatformCategory;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.domain.UserInterest;
import com.example.nexus.app.user.dto.UserInterestDto;
import com.example.nexus.app.user.repository.UserInterestRepository;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInterestService {

    private final UserInterestRepository userInterestRepository;
    private final UserRepository userRepository;

    /**
     * 사용자 관심사 조회
     */
    public UserInterestDto.UserInterestResponse getUserInterest(Long userId) {
        UserInterest userInterest = userInterestRepository.findByUserId(userId).orElse(null);
        
        if (userInterest == null) {
            return new UserInterestDto.UserInterestResponse(
                    new HashSet<>(), new HashSet<>(), new HashSet<>()
            );
        }

        return new UserInterestDto.UserInterestResponse(
                userInterest.getMainCategories().stream()
                        .map(MainCategory::name)
                        .collect(Collectors.toSet()),
                userInterest.getPlatformCategories().stream()
                        .map(PlatformCategory::name)
                        .collect(Collectors.toSet()),
                userInterest.getGenreCategories().stream()
                        .map(GenreCategory::getCode)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * 사용자 관심사 설정
     */
    @Transactional
    public UserInterestDto.UserInterestResponse setUserInterest(
            Long userId, 
            UserInterestDto.UserInterestRequest request) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 카테고리 변환
        Set<MainCategory> mainCategories = request.mainCategories().stream()
                .map(MainCategory::valueOf)
                .collect(Collectors.toSet());

        Set<PlatformCategory> platformCategories = request.platformCategories().stream()
                .map(PlatformCategory::valueOf)
                .collect(Collectors.toSet());

        Set<GenreCategory> genreCategories = request.genreCategories().stream()
                .map(this::findGenreCategoryByCode)
                .collect(Collectors.toSet());

        // 기존 관심사 조회 또는 새로 생성
        UserInterest userInterest = userInterestRepository.findByUserId(userId)
                .orElse(UserInterest.builder()
                        .user(user)
                        .mainCategories(mainCategories)
                        .platformCategories(platformCategories)
                        .genreCategories(genreCategories)
                        .build());

        // 관심사 업데이트
        userInterest.updateInterests(mainCategories, platformCategories, genreCategories);
        
        UserInterest savedInterest = userInterestRepository.save(userInterest);

        return new UserInterestDto.UserInterestResponse(
                savedInterest.getMainCategories().stream()
                        .map(MainCategory::name)
                        .collect(Collectors.toSet()),
                savedInterest.getPlatformCategories().stream()
                        .map(PlatformCategory::name)
                        .collect(Collectors.toSet()),
                savedInterest.getGenreCategories().stream()
                        .map(GenreCategory::getCode)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * 코드로 GenreCategory 찾기
     */
    private GenreCategory findGenreCategoryByCode(String code) {
        for (GenreCategory category : GenreCategory.values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 장르 카테고리 코드: " + code);
    }
}
