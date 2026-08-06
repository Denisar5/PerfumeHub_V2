package com.denisar5.perfumehub.service;

import com.denisar5.perfumehub.dto.request.ReviewCreateDto;
import com.denisar5.perfumehub.dto.request.ReviewEditDto;
import com.denisar5.perfumehub.dto.response.ReviewViewDto;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    UUID createReview(
            String username,
            ReviewCreateDto reviewCreateDto
    );

    void editOwnReview(
            UUID reviewId,
            String username,
            ReviewEditDto reviewEditDto
    );

    void deleteOwnReview(
            UUID reviewId,
            String username
    );

    void approveReview(UUID reviewId);

    void rejectReview(UUID reviewId);

    List<ReviewViewDto> getApprovedReviewsForPerfume(
            UUID perfumeId
    );

    List<ReviewViewDto> getReviewsForUser(String username);

    List<ReviewViewDto> getPendingReviews();
}