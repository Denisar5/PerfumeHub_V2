package com.denisar5.review_service.controller;

import com.denisar5.review_service.dto.ReviewCreateRequest;
import com.denisar5.review_service.dto.ReviewResponse;
import com.denisar5.review_service.dto.ReviewStatisticsResponse;
import com.denisar5.review_service.dto.ReviewUpdateRequest;
import com.denisar5.review_service.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewRestController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        ReviewResponse response =
                reviewService.createReview(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable UUID reviewId,
            @RequestParam String username,
            @Valid @RequestBody ReviewUpdateRequest request
    ) {
        return ResponseEntity.ok(
                reviewService.updateReview(
                        reviewId,
                        username,
                        request
                )
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID reviewId,
            @RequestParam String username
    ) {
        reviewService.deleteReview(reviewId, username);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{reviewId}/approve")
    public ResponseEntity<ReviewResponse> approveReview(
            @PathVariable UUID reviewId
    ) {
        return ResponseEntity.ok(
                reviewService.approveReview(reviewId)
        );
    }

    @DeleteMapping("/{reviewId}/reject")
    public ResponseEntity<Void> rejectReview(
            @PathVariable UUID reviewId
    ) {
        reviewService.rejectReview(reviewId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/perfume/{perfumeId}")
    public ResponseEntity<List<ReviewResponse>>
    getApprovedReviewsForPerfume(
            @PathVariable UUID perfumeId
    ) {
        return ResponseEntity.ok(
                reviewService.getApprovedReviewsForPerfume(perfumeId)
        );
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<ReviewResponse>> getReviewsForUser(
            @PathVariable String username
    ) {
        return ResponseEntity.ok(
                reviewService.getReviewsForUser(username)
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ReviewResponse>> getPendingReviews() {
        return ResponseEntity.ok(
                reviewService.getPendingReviews()
        );
    }

    @GetMapping("/latest")
    public ResponseEntity<List<ReviewResponse>> getLatestReviews() {
        return ResponseEntity.ok(
                reviewService.getLatestReviews()
        );
    }

    @GetMapping("/statistics")
    public ResponseEntity<ReviewStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(
                reviewService.getStatistics()
        );
    }

    @GetMapping("/perfume/{perfumeId}/exists")
    public ResponseEntity<Boolean> hasReviewsForPerfume(
            @PathVariable UUID perfumeId
    ) {
        return ResponseEntity.ok(
                reviewService.hasReviewsForPerfume(perfumeId)
        );
    }
}