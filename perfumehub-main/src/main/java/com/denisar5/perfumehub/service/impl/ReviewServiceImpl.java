package com.denisar5.perfumehub.service.impl;

import com.denisar5.perfumehub.client.ReviewClient;
import com.denisar5.perfumehub.client.dto.ReviewCreateRequest;
import com.denisar5.perfumehub.client.dto.ReviewResponse;
import com.denisar5.perfumehub.client.dto.ReviewUpdateRequest;
import com.denisar5.perfumehub.dto.request.ReviewCreateDto;
import com.denisar5.perfumehub.dto.request.ReviewEditDto;
import com.denisar5.perfumehub.dto.response.ReviewViewDto;
import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.entity.UserEntity;
import com.denisar5.perfumehub.exception.ResourceNotFoundException;
import com.denisar5.perfumehub.repository.PerfumeRepository;
import com.denisar5.perfumehub.repository.UserRepository;
import com.denisar5.perfumehub.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewClient reviewClient;
    private final UserRepository userRepository;
    private final PerfumeRepository perfumeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewViewDto> getApprovedReviewsForPerfume(UUID perfumeId) {
        return reviewClient
                .getApprovedReviewsForPerfume(perfumeId)
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewViewDto> getReviewsForUser(String username) {
        return reviewClient
                .getReviewsForUser(username)
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewViewDto> getPendingReviews() {
        return reviewClient
                .getPendingReviews()
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    @Override
    @Transactional
    public UUID createReview(
            String username,
            ReviewCreateDto reviewCreateDto
    ) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Perfume perfume = perfumeRepository
                .findById(reviewCreateDto.getPerfumeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Perfume not found")
                );

        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .perfumeId(perfume.getId())
                .perfumeName(perfume.getName())
                .userId(user.getId())
                .username(user.getUsername())
                .rating(reviewCreateDto.getRating())
                .content(reviewCreateDto.getContent())
                .build();

        ReviewResponse response =
                reviewClient.createReview(request);

        log.info(
                "Created review through microservice id={} username={} perfumeId={}",
                response.getId(),
                username,
                perfume.getId()
        );

        return response.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewEditDto getReviewEditDto(
            UUID reviewId,
            String username
    ) {
        ReviewResponse review = reviewClient
                .getReviewsForUser(username)
                .stream()
                .filter(item -> item.getId().equals(reviewId))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found"
                        )
                );

        ReviewEditDto dto = new ReviewEditDto();
        dto.setRating(review.getRating());
        dto.setContent(review.getContent());

        return dto;
    }

    @Override
    @Transactional
    public void editOwnReview(
            UUID reviewId,
            String username,
            ReviewEditDto reviewEditDto
    ) {
        ReviewUpdateRequest request =
                ReviewUpdateRequest.builder()
                        .rating(reviewEditDto.getRating())
                        .content(reviewEditDto.getContent())
                        .build();

        reviewClient.updateReview(
                reviewId,
                username,
                request
        );

        log.info(
                "Updated review through microservice id={} username={}",
                reviewId,
                username
        );
    }

    @Override
    @Transactional
    public void deleteOwnReview(
            UUID reviewId,
            String username
    ) {
        reviewClient.deleteReview(
                reviewId,
                username
        );

        log.info(
                "Deleted review through microservice id={} username={}",
                reviewId,
                username
        );
    }

    @Override
    @Transactional
    public void approveReview(UUID reviewId) {
        reviewClient.approveReview(reviewId);

        log.info(
                "Approved review through microservice id={}",
                reviewId
        );
    }

    @Override
    @Transactional
    public void rejectReview(UUID reviewId) {
        reviewClient.rejectReview(reviewId);

        log.info(
                "Rejected review through microservice id={}",
                reviewId
        );
    }

    private ReviewViewDto mapToViewDto(
            ReviewResponse review
    ) {
        return ReviewViewDto.builder()
                .id(review.getId())
                .perfumeId(review.getPerfumeId())
                .perfumeName(review.getPerfumeName())
                .username(review.getUsername())
                .rating(review.getRating())
                .content(review.getContent())
                .approved(review.isApproved())
                .createdAt(review.getCreatedAt())
                .build();
    }
}