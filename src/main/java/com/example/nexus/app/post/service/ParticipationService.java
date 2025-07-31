package com.example.nexus.app.post.service;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.post.controller.dto.request.ParticipationApplicationRequest;
import com.example.nexus.app.post.controller.dto.response.ParticipationResponse;
import com.example.nexus.app.post.domain.Participation;
import com.example.nexus.app.post.domain.ParticipationStatus;
import com.example.nexus.app.post.domain.Post;
import com.example.nexus.app.post.domain.dto.ParticipationApplicationDto;
import com.example.nexus.app.post.repository.ParticipationRepository;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostUserStatusService postUserStatusService;

    // 참가 신청
    @Transactional
    public ParticipationResponse applyForParticipation(Long postId, Long userId, ParticipationApplicationRequest request) {
        Post post = getPost(postId);
        User user = getUser(userId);
        validatePostForApplication(post);
        validateDuplicateApplication(postId, userId);
        validateParticipantLimit(post);

        // Controller DTO -> Domain DTO 변환
        ParticipationApplicationDto applicationDto = ParticipationApplicationDto.from(request);

        Participation participation = Participation.createApplication(post, user, applicationDto);
        Participation savedParticipation = participationRepository.save(participation);

        PostUserStatusService.PostUserStatus status = postUserStatusService.getPostUserStatus(postId, userId);

        return ParticipationResponse.from(savedParticipation, status.isLiked(), status.isParticipated());
    }

    // 참여 신청한 게시글 조회
    public Page<ParticipationResponse> getMyApplications(Long userId, Pageable pageable) {
        getUser(userId);

        Page<Participation> participations = participationRepository.findByUserIdWithPost(userId, pageable);

        return mapParticipationsWithUserStatus(participations, userId);
    }

    // 참여 신청한 게시글 상태별 조회
    public Page<ParticipationResponse> getMyApplications(Long userId, ParticipationStatus status ,Pageable pageable) {
        getUser(userId);

        Page<Participation> participations = participationRepository.findByUserIdAndStatusWithPost(userId, status, pageable);

        return mapParticipationsWithUserStatus(participations, userId);
    }

    // 게시글에 대한 참가 신청자 조회
    public Page<ParticipationResponse> getPostApplications(Long postId, Long userId, Pageable pageable) {
        Post post = getPost(postId);
        validatePostOwnership(post, userId);

        Page<Participation> applications = participationRepository.findByPostIdWithUser(postId, pageable);

        return mapParticipationsWithUserStatus(applications, userId);
    }

    // 게시글에 대한 참가 신청자 상태별 조회
    public Page<ParticipationResponse> getPostApplications(Long postId, Long userId, ParticipationStatus status,
                                                           Pageable pageable) {
        Post post = getPost(postId);
        validatePostOwnership(post, userId);

        Page<Participation> applications = participationRepository.findByPostIdAndStatusWithUser(postId, status, pageable);

        return mapParticipationsWithUserStatus(applications, userId);
    }

    @Transactional
    public void approveApplication(Long participationId, Long userId) {
        Participation participation = getParticipation(participationId);
        validateParticipationOwnershipAndStatus(participation, userId);
        participation.approve();
        participation.getPost().incrementParticipants();
    }

    @Transactional
    public void rejectApplication(Long participationId, Long userId) {
        Participation participation = getParticipation(participationId);
        validateParticipationOwnershipAndStatus(participation, userId);
        participation.reject();
    }

    @Transactional
    public void cancelApplication(Long participationId, Long userId) {
        Participation participation = getParticipation(participationId);

        if (!participation.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.APPLICATION_ACCESS_DENIED);
        }

        if (!participation.isPending() && !participation.isApproved()) {
            throw new GeneralException(ErrorStatus.CANNOT_CANCEL_APPLICATION);
        }

        if (participation.isApproved()) {
            participation.getPost().decrementParticipants();
        }

        participationRepository.delete(participation);
    }

    public boolean hasApplied(Long postId, Long userId) {
        return participationRepository.existsByUserIdAndPostId(userId, postId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }

    private void validatePostForApplication(Post post) {
        if (!post.isActive()) {
            throw new GeneralException(ErrorStatus.POST_NOT_ACTIVE);
        }

        if (post.isExpired()) {
            throw new GeneralException(ErrorStatus.POST_EXPIRED);
        }
    }

    private void validateDuplicateApplication(Long postId, Long userId) {
        if (participationRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new GeneralException(ErrorStatus.ALREADY_APPLIED);
        }
    }

    private void validateParticipantLimit(Post post) {
        if (!post.canParticipate()) {
            throw new GeneralException(ErrorStatus.PARTICIPATION_LIMIT_EXCEEDED);
        }
    }

    private void validatePostOwnership(Post post, Long userId) {
        if (!post.isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }
    }

    private Participation getParticipation(Long participationId) {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new
                        GeneralException(ErrorStatus.PARTICIPATION_NOT_FOUND));
    }

    private void validateParticipationOwnershipAndStatus(Participation participation, Long userId) {
        if (!participation.getPost().isOwner(userId)) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }

        if (!participation.isPending()) {
            throw new GeneralException(ErrorStatus.ALREADY_PROCESSED_APPLICATION);
        }
    }

    private Page<ParticipationResponse> mapParticipationsWithUserStatus(Page<Participation> participations, Long userId) {
        List<Long> postIds = participations.getContent().stream()
                .map(participation -> participation.getPost().getId())
                .toList();
        Map<Long, PostUserStatusService.PostUserStatus> statusMap =
                postUserStatusService.getPostUserStatuses(postIds, userId);

        return participations.map(participation -> {
            PostUserStatusService.PostUserStatus status =
                    statusMap.get(participation.getPost().getId());
            return ParticipationResponse.from(participation, status.isLiked(),
                    status.isParticipated());
        });
    }
}
