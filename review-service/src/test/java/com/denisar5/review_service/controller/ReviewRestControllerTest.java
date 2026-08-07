package com.denisar5.review_service.controller;

import com.denisar5.review_service.dto.ReviewCreateRequest;
import com.denisar5.review_service.dto.ReviewResponse;
import com.denisar5.review_service.dto.ReviewStatisticsResponse;
import com.denisar5.review_service.dto.ReviewUpdateRequest;
import com.denisar5.review_service.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewRestController.class)
class ReviewRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @Test
    void getPendingReviewsShouldReturnOk() throws Exception {

        ReviewResponse review = sampleReview();

        when(reviewService.getPendingReviews())
                .thenReturn(List.of(review));

        mockMvc.perform(
                        get("/api/reviews/pending")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username")
                        .value("denis"))
                .andExpect(jsonPath("$[0].rating")
                        .value(5))
                .andExpect(jsonPath("$[0].approved")
                        .value(false));
    }

    @Test
    void createReviewShouldReturnCreated() throws Exception {

        UUID perfumeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setPerfumeId(perfumeId);
        request.setPerfumeName("Dior Sauvage");
        request.setUserId(userId);
        request.setUsername("denis");
        request.setRating(5);
        request.setContent(
                "Excellent fragrance with strong performance."
        );

        when(reviewService.createReview(any(ReviewCreateRequest.class)))
                .thenReturn(sampleReview());

        mockMvc.perform(
                        post("/api/reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username")
                        .value("denis"))
                .andExpect(jsonPath("$.rating")
                        .value(5));
    }

    @Test
    void createReviewShouldReturnBadRequestWhenInvalid() throws Exception {

        ReviewCreateRequest request = new ReviewCreateRequest();

        request.setUsername("");
        request.setRating(10);
        request.setContent("short");

        mockMvc.perform(
                        post("/api/reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors")
                        .exists());
    }

    @Test
    void updateReviewShouldReturnOk() throws Exception {

        UUID reviewId = UUID.randomUUID();

        ReviewUpdateRequest request =
                new ReviewUpdateRequest();

        request.setRating(4);
        request.setContent(
                "Updated review content with enough characters."
        );

        ReviewResponse updated = ReviewResponse.builder()
                .id(reviewId)
                .perfumeId(UUID.randomUUID())
                .perfumeName("Dior Sauvage")
                .userId(UUID.randomUUID())
                .username("denis")
                .rating(4)
                .content(request.getContent())
                .approved(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(reviewService.updateReview(
                eq(reviewId),
                eq("denis"),
                any(ReviewUpdateRequest.class)
        )).thenReturn(updated);

        mockMvc.perform(
                        put("/api/reviews/{reviewId}", reviewId)
                                .param("username", "denis")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating")
                        .value(4))
                .andExpect(jsonPath("$.approved")
                        .value(false));
    }

    @Test
    void deleteReviewShouldReturnNoContent() throws Exception {

        UUID reviewId = UUID.randomUUID();

        mockMvc.perform(
                        delete("/api/reviews/{reviewId}", reviewId)
                                .param("username", "denis")
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void approveReviewShouldReturnOk() throws Exception {

        UUID reviewId = UUID.randomUUID();

        ReviewResponse approved =
                ReviewResponse.builder()
                        .id(reviewId)
                        .perfumeId(UUID.randomUUID())
                        .perfumeName("Dior Sauvage")
                        .userId(UUID.randomUUID())
                        .username("denis")
                        .rating(5)
                        .content(
                                "Excellent fragrance with strong performance."
                        )
                        .approved(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(reviewService.approveReview(reviewId))
                .thenReturn(approved);

        mockMvc.perform(
                        put("/api/reviews/{reviewId}/approve", reviewId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approved")
                        .value(true));
    }

    @Test
    void rejectReviewShouldReturnNoContent() throws Exception {

        UUID reviewId = UUID.randomUUID();

        mockMvc.perform(
                        delete("/api/reviews/{reviewId}/reject", reviewId)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void statisticsShouldReturnValues() throws Exception {

        ReviewStatisticsResponse statistics =
                ReviewStatisticsResponse.builder()
                        .totalReviews(10)
                        .approvedReviews(7)
                        .pendingReviews(3)
                        .build();

        when(reviewService.getStatistics())
                .thenReturn(statistics);

        mockMvc.perform(
                        get("/api/reviews/statistics")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReviews")
                        .value(10))
                .andExpect(jsonPath("$.approvedReviews")
                        .value(7))
                .andExpect(jsonPath("$.pendingReviews")
                        .value(3));
    }

    private ReviewResponse sampleReview() {

        return ReviewResponse.builder()
                .id(UUID.randomUUID())
                .perfumeId(UUID.randomUUID())
                .perfumeName("Dior Sauvage")
                .userId(UUID.randomUUID())
                .username("denis")
                .rating(5)
                .content(
                        "Excellent fragrance with strong performance."
                )
                .approved(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}