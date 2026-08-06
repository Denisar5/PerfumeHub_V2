package com.denisar5.perfumehub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public String getPendingReviews(Model model) {
        model.addAttribute(
                "reviews",
                reviewService.getPendingReviews()
        );

        return "admin/reviews";
    }

    @PostMapping("/{reviewId}/approve")
    public String approveReview(
            @PathVariable UUID reviewId
    ) {
        reviewService.approveReview(reviewId);

        return "redirect:/admin/reviews?approved=true";
    }

    @PostMapping("/{reviewId}/reject")
    public String rejectReview(
            @PathVariable UUID reviewId
    ) {
        reviewService.rejectReview(reviewId);

        return "redirect:/admin/reviews?rejected=true";
    }
}