package com.denisar5.review_service.service;

import com.denisar5.review_service.dto.ReviewCreateRequest;
import com.denisar5.review_service.dto.ReviewResponse;
import com.denisar5.review_service.dto.ReviewUpdateRequest;
import com.denisar5.review_service.entity.Review;
import com.denisar5.review_service.exception.DuplicateReviewException;
import com.denisar5.review_service.exception.ReviewNotFoundException;
import com.denisar5.review_service.repository.ReviewRepository;
import com.denisar5.review_service.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private UUID reviewId;
    private UUID perfumeId;
    private UUID userId;
    private Review review;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        perfumeId = UUID.randomUUID();
        userId = UUID.randomUUID();

        review = Review.builder()
                .id(reviewId)
                .perfumeId(perfumeId)
                .perfumeName("Dior Sauvage")
                .userId(userId)
                .username("denis")
                .rating(5)
                .content("Excellent fragrance with very good longevity.")
                .approved(false)
                .build();
    }

    @Test
    void createReviewShouldSaveReview() {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setPerfumeId(perfumeId);
        request.setPerfumeName("Dior Sauvage");
        request.setUserId(userId);
        request.setUsername("denis");
        request.setRating(5);
        request.setContent("Excellent fragrance with very good longevity.");

        when(reviewRepository.existsByUsernameAndPerfumeId(
                "denis",
                perfumeId
        )).thenReturn(false);

        when(reviewRepository.save(any(Review.class)))
                .thenAnswer(invocation -> {
                    Review saved = invocation.getArgument(0);
                    saved.setId(reviewId);
                    return saved;
                });

        ReviewResponse response =
                reviewService.createReview(request);

        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        assertEquals("denis", response.getUsername());
        assertEquals(5, response.getRating());
        assertFalse(response.isApproved());

        verify(reviewRepository)
                .save(any(Review.class));
    }

    @Test
    void createReviewShouldThrowWhenDuplicateExists() {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setPerfumeId(perfumeId);
        request.setPerfumeName("Dior Sauvage");
        request.setUserId(userId);
        request.setUsername("denis");
        request.setRating(5);
        request.setContent("Excellent fragrance with very good longevity.");

        when(reviewRepository.existsByUsernameAndPerfumeId(
                "denis",
                perfumeId
        )).thenReturn(true);

        assertThrows(
                DuplicateReviewException.class,
                () -> reviewService.createReview(request)
        );

        verify(reviewRepository, never())
                .save(any(Review.class));
    }

    @Test
    void updateReviewShouldChangeRatingAndContent() {
        ReviewUpdateRequest request = new ReviewUpdateRequest();
        request.setRating(4);
        request.setContent(
                "Updated review content with enough characters."
        );

        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(review));

        ReviewResponse response =
                reviewService.updateReview(
                        reviewId,
                        "denis",
                        request
                );

        assertEquals(4, response.getRating());
        assertEquals(
                "Updated review content with enough characters.",
                response.getContent()
        );

        assertFalse(response.isApproved());
    }

    @Test
    void approveReviewShouldSetApprovedTrue() {
        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(review));

        ReviewResponse response =
                reviewService.approveReview(reviewId);

        assertTrue(response.isApproved());
        assertTrue(review.isApproved());
    }

    @Test
    void rejectReviewShouldDeleteReview() {
        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(review));

        reviewService.rejectReview(reviewId);

        verify(reviewRepository)
                .delete(review);
    }

    @Test
    void missingReviewShouldThrowException() {
        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.empty());

        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.approveReview(reviewId)
        );
    }
}