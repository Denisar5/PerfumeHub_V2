package com.denisar5.perfumehub.service.impl;

import com.denisar5.perfumehub.dto.request.ReviewCreateDto;
import com.denisar5.perfumehub.dto.request.ReviewEditDto;
import com.denisar5.perfumehub.dto.response.ReviewViewDto;
import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.entity.Review;
import com.denisar5.perfumehub.entity.UserEntity;
import com.denisar5.perfumehub.exception.DuplicateResourceException;
import com.denisar5.perfumehub.exception.ResourceNotFoundException;
import com.denisar5.perfumehub.exception.UnauthorizedOperationException;
import com.denisar5.perfumehub.repository.PerfumeRepository;
import com.denisar5.perfumehub.repository.ReviewRepository;
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

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PerfumeRepository perfumeRepository;

    @Override
    @Transactional
    public UUID createReview(
            String username,
            ReviewCreateDto dto
    ) {
        UserEntity user = findUserByUsername(username);
        Perfume perfume = findPerfumeById(dto.getPerfumeId());

        if (reviewRepository.existsByUserAndPerfume(user, perfume)) {
            throw new DuplicateResourceException(
                    "You have already reviewed this perfume"
            );
        }

        Review review = Review.builder()
                .user(user)
                .perfume(perfume)
                .rating(dto.getRating())
                .content(dto.getContent().trim())
                .approved(false)
                .build();

        reviewRepository.save(review);

        log.info(
                "User username={} created review id={} for perfume id={}",
                username,
                review.getId(),
                perfume.getId()
        );

        return review.getId();
    }

    @Override
    @Transactional
    public void editOwnReview(
            UUID reviewId,
            String username,
            ReviewEditDto dto
    ) {
        Review review = findReviewById(reviewId);

        validateOwnership(review, username);

        review.setRating(dto.getRating());
        review.setContent(dto.getContent().trim());

        // An edited review must be approved again.
        review.setApproved(false);

        log.info(
                "User username={} edited review id={}",
                username,
                reviewId
        );
    }

    @Override
    @Transactional
    public void deleteOwnReview(
            UUID reviewId,
            String username
    ) {
        Review review = findReviewById(reviewId);

        validateOwnership(review, username);

        reviewRepository.delete(review);

        log.info(
                "User username={} deleted review id={}",
                username,
                reviewId
        );
    }

    @Override
    @Transactional
    public void approveReview(UUID reviewId) {
        Review review = findReviewById(reviewId);

        review.setApproved(true);

        log.info("Approved review id={}", reviewId);
    }

    @Override
    @Transactional
    public void rejectReview(UUID reviewId) {
        Review review = findReviewById(reviewId);

        reviewRepository.delete(review);

        log.info("Rejected and deleted review id={}", reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewViewDto> getApprovedReviewsForPerfume(
            UUID perfumeId
    ) {
        Perfume perfume = findPerfumeById(perfumeId);

        return reviewRepository
                .findByPerfumeAndApprovedTrueOrderByCreatedAtDesc(perfume)
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewViewDto> getReviewsForUser(String username) {
        UserEntity user = findUserByUsername(username);

        return reviewRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewViewDto> getPendingReviews() {
        return reviewRepository
                .findByApprovedFalseOrderByCreatedAtAsc()
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    private void validateOwnership(
            Review review,
            String username
    ) {
        if (!review.getUser().getUsername().equals(username)) {
            throw new UnauthorizedOperationException(
                    "You cannot modify another user's review"
            );
        }
    }

    private Review findReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review not found"
                ));
    }

    private UserEntity findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));
    }

    private Perfume findPerfumeById(UUID perfumeId) {
        return perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfume not found"
                ));
    }

    private ReviewViewDto mapToViewDto(Review review) {
        return ReviewViewDto.builder()
                .id(review.getId())
                .perfumeId(review.getPerfume().getId())
                .perfumeName(review.getPerfume().getName())
                .username(review.getUser().getUsername())
                .rating(review.getRating())
                .content(review.getContent())
                .approved(review.isApproved())
                .createdAt(review.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewEditDto getReviewEditDto(
            UUID reviewId,
            String username
    ) {
        Review review = findReviewById(reviewId);

        validateOwnership(review, username);

        ReviewEditDto dto = new ReviewEditDto();
        dto.setRating(review.getRating());
        dto.setContent(review.getContent());

        return dto;
    }
}