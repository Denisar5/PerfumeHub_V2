package com.denisar5.review_service.service.impl;

import com.denisar5.review_service.dto.ReviewCreateRequest;
import com.denisar5.review_service.dto.ReviewResponse;
import com.denisar5.review_service.dto.ReviewStatisticsResponse;
import com.denisar5.review_service.dto.ReviewUpdateRequest;
import com.denisar5.review_service.entity.Review;
import com.denisar5.review_service.exception.DuplicateReviewException;
import com.denisar5.review_service.exception.ReviewNotFoundException;
import com.denisar5.review_service.exception.UnauthorizedReviewOperationException;
import com.denisar5.review_service.repository.ReviewRepository;
import com.denisar5.review_service.service.ReviewService;
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

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request) {
        String username = normalizeUsername(request.getUsername());

        if (reviewRepository.existsByUsernameAndPerfumeId(
                username,
                request.getPerfumeId()
        )) {
            throw new DuplicateReviewException(
                    "The user has already reviewed this perfume"
            );
        }

        Review review = Review.builder()
                .perfumeId(request.getPerfumeId())
                .perfumeName(request.getPerfumeName().trim())
                .userId(request.getUserId())
                .username(username)
                .rating(request.getRating())
                .content(request.getContent().trim())
                .approved(false)
                .build();

        Review savedReview = reviewRepository.save(review);

        log.info(
                "Created review id={} by username={} for perfumeId={}",
                savedReview.getId(),
                savedReview.getUsername(),
                savedReview.getPerfumeId()
        );

        return mapToResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(
            UUID reviewId,
            String username,
            ReviewUpdateRequest request
    ) {
        Review review = findReviewById(reviewId);

        validateOwnership(review, username);

        review.setRating(request.getRating());
        review.setContent(request.getContent().trim());

        // Edited reviews must be moderated again.
        review.setApproved(false);

        log.info(
                "Updated review id={} by username={}",
                reviewId,
                username
        );

        return mapToResponse(review);
    }

    @Override
    @Transactional
    public void deleteReview(
            UUID reviewId,
            String username
    ) {
        Review review = findReviewById(reviewId);

        validateOwnership(review, username);

        reviewRepository.delete(review);

        log.info(
                "Deleted review id={} by username={}",
                reviewId,
                username
        );
    }

    @Override
    @Transactional
    public ReviewResponse approveReview(UUID reviewId) {
        Review review = findReviewById(reviewId);

        review.setApproved(true);

        log.info("Approved review id={}", reviewId);

        return mapToResponse(review);
    }

    @Override
    @Transactional
    public void rejectReview(UUID reviewId) {
        Review review = findReviewById(reviewId);

        reviewRepository.delete(review);

        log.info("Rejected review id={}", reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getApprovedReviewsForPerfume(
            UUID perfumeId
    ) {
        return reviewRepository
                .findByPerfumeIdAndApprovedTrueOrderByCreatedAtDesc(perfumeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsForUser(String username) {
        return reviewRepository
                .findByUsernameOrderByCreatedAtDesc(
                        normalizeUsername(username)
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getPendingReviews() {
        return reviewRepository
                .findByApprovedFalseOrderByCreatedAtAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getLatestReviews() {
        return reviewRepository
                .findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewStatisticsResponse getStatistics() {
        return ReviewStatisticsResponse.builder()
                .totalReviews(reviewRepository.count())
                .approvedReviews(reviewRepository.countByApprovedTrue())
                .pendingReviews(reviewRepository.countByApprovedFalse())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasReviewsForPerfume(UUID perfumeId) {
        return reviewRepository.existsByPerfumeId(perfumeId);
    }

    private Review findReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(
                        "Review with id " + reviewId + " was not found"
                ));
    }

    private void validateOwnership(
            Review review,
            String username
    ) {
        if (!review.getUsername().equalsIgnoreCase(username.trim())) {
            throw new UnauthorizedReviewOperationException(
                    "The review does not belong to this user"
            );
        }
    }

    private String normalizeUsername(String username) {
        return username.trim();
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .perfumeId(review.getPerfumeId())
                .perfumeName(review.getPerfumeName())
                .userId(review.getUserId())
                .username(review.getUsername())
                .rating(review.getRating())
                .content(review.getContent())
                .approved(review.isApproved())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}