package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.ReviewEditDto;
import com.denisar5.perfumehub.service.PerfumeService;
import com.denisar5.perfumehub.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private PerfumeService perfumeService;

    @Test
    @WithMockUser(username = "denis", roles = "USER")
    void getMyReviewsShouldReturnPage() throws Exception {

        when(reviewService.getReviewsForUser("denis"))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/reviews/my")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("profile/my-reviews"))
                .andExpect(model().attributeExists("reviews"));

        verify(reviewService)
                .getReviewsForUser("denis");
    }

    @Test
    @WithMockUser(username = "denis", roles = "USER")
    void getEditReviewShouldReturnEditPage() throws Exception {

        UUID reviewId = UUID.randomUUID();

        ReviewEditDto dto = new ReviewEditDto();
        dto.setRating(5);
        dto.setContent(
                "Excellent fragrance with strong longevity."
        );

        when(reviewService.getReviewEditDto(
                reviewId,
                "denis"
        )).thenReturn(dto);

        mockMvc.perform(
                        get(
                                "/reviews/{reviewId}/edit",
                                reviewId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(view().name("profile/review-edit"))
                .andExpect(model().attributeExists("reviewEditDto"))
                .andExpect(model().attribute("reviewId", reviewId));
    }

    @Test
    @WithMockUser(username = "denis", roles = "USER")
    void editReviewShouldRedirectAfterSuccess() throws Exception {

        UUID reviewId = UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/reviews/{reviewId}/edit",
                                reviewId
                        )
                                .with(csrf())
                                .param("rating", "4")
                                .param(
                                        "content",
                                        "Updated review with enough characters."
                                )
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(
                        redirectedUrl(
                                "/reviews/my?updated=true"
                        )
                );

        verify(reviewService)
                .editOwnReview(
                        eq(reviewId),
                        eq("denis"),
                        any()
                );
    }

    @Test
    @WithMockUser(username = "denis", roles = "USER")
    void deleteReviewShouldRedirectAfterSuccess() throws Exception {

        UUID reviewId = UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/reviews/{reviewId}/delete",
                                reviewId
                        )
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(
                        redirectedUrl(
                                "/reviews/my?deleted=true"
                        )
                );

        verify(reviewService)
                .deleteOwnReview(
                        reviewId,
                        "denis"
                );
    }
}