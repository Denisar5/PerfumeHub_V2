package com.denisar5.perfumehub.client;

import com.denisar5.perfumehub.client.dto.ReviewCreateRequest;
import com.denisar5.perfumehub.client.dto.ReviewResponse;
import com.denisar5.perfumehub.client.dto.ReviewStatisticsResponse;
import com.denisar5.perfumehub.client.dto.ReviewUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "review-service",
        url = "${review.service.url}"
)
public interface ReviewClient {

    @GetMapping("/api/reviews/perfume/{perfumeId}")
    List<ReviewResponse> getApprovedReviewsForPerfume(
            @PathVariable UUID perfumeId
    );

    @GetMapping("/api/reviews/user/{username}")
    List<ReviewResponse> getReviewsForUser(
            @PathVariable String username
    );

    @GetMapping("/api/reviews/pending")
    List<ReviewResponse> getPendingReviews();

    @GetMapping("/api/reviews/latest")
    List<ReviewResponse> getLatestReviews();

    @GetMapping("/api/reviews/statistics")
    ReviewStatisticsResponse getStatistics();

    @GetMapping("/api/reviews/perfume/{perfumeId}/exists")
    boolean hasReviewsForPerfume(
            @PathVariable UUID perfumeId
    );

    @PostMapping("/api/reviews")
    ReviewResponse createReview(
            @RequestBody ReviewCreateRequest request
    );

    @PutMapping("/api/reviews/{reviewId}")
    ReviewResponse updateReview(
            @PathVariable UUID reviewId,
            @RequestParam String username,
            @RequestBody ReviewUpdateRequest request
    );

    @DeleteMapping("/api/reviews/{reviewId}")
    void deleteReview(
            @PathVariable UUID reviewId,
            @RequestParam String username
    );

    @PutMapping("/api/reviews/{reviewId}/approve")
    ReviewResponse approveReview(
            @PathVariable UUID reviewId
    );

    @DeleteMapping("/api/reviews/{reviewId}/reject")
    void rejectReview(
            @PathVariable UUID reviewId
    );
}