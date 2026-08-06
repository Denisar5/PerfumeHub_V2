package com.denisar5.review_service.service;

import com.denisar5.review_service.dto.ReviewCreateRequest;
import com.denisar5.review_service.dto.ReviewResponse;
import com.denisar5.review_service.dto.ReviewStatisticsResponse;
import com.denisar5.review_service.dto.ReviewUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    ReviewResponse createReview(ReviewCreateRequest request);

    ReviewResponse updateReview(
            UUID reviewId,
            String username,
            ReviewUpdateRequest request
    );

    void deleteReview(
            UUID reviewId,
            String username
    );

    ReviewResponse approveReview(UUID reviewId);

    void rejectReview(UUID reviewId);

    List<ReviewResponse> getApprovedReviewsForPerfume(UUID perfumeId);

    List<ReviewResponse> getReviewsForUser(String username);

    List<ReviewResponse> getPendingReviews();

    List<ReviewResponse> getLatestReviews();

    ReviewStatisticsResponse getStatistics();

    boolean hasReviewsForPerfume(UUID perfumeId);
}