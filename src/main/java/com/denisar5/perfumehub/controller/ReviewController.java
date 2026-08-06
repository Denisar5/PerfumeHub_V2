package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.ReviewCreateDto;
import com.denisar5.perfumehub.dto.request.ReviewEditDto;
import com.denisar5.perfumehub.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/my")
    public String getMyReviews(
            Authentication authentication,
            Model model
    ) {
        model.addAttribute(
                "reviews",
                reviewService.getReviewsForUser(
                        authentication.getName()
                )
        );

        return "profile/my-reviews";
    }

    @PostMapping
    public String createReview(
            @Valid @ModelAttribute("reviewCreateDto")
            ReviewCreateDto reviewCreateDto,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            return "redirect:/perfumes/"
                    + reviewCreateDto.getPerfumeId()
                    + "?reviewError=true";
        }

        reviewService.createReview(
                authentication.getName(),
                reviewCreateDto
        );

        return "redirect:/perfumes/"
                + reviewCreateDto.getPerfumeId()
                + "?reviewSubmitted=true";
    }

    @GetMapping("/{reviewId}/edit")
    public String getEditReview(
            @PathVariable UUID reviewId,
            Authentication authentication,
            Model model
    ) {
        model.addAttribute(
                "reviewEditDto",
                reviewService.getReviewEditDto(
                        reviewId,
                        authentication.getName()
                )
        );

        model.addAttribute("reviewId", reviewId);

        return "profile/review-edit";
    }

    @PostMapping("/{reviewId}/edit")
    public String editReview(
            @PathVariable UUID reviewId,
            @Valid @ModelAttribute("reviewEditDto")
            ReviewEditDto reviewEditDto,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("reviewId", reviewId);
            return "profile/review-edit";
        }

        reviewService.editOwnReview(
                reviewId,
                authentication.getName(),
                reviewEditDto
        );

        return "redirect:/reviews/my?updated=true";
    }

    @PostMapping("/{reviewId}/delete")
    public String deleteReview(
            @PathVariable UUID reviewId,
            Authentication authentication
    ) {
        reviewService.deleteOwnReview(
                reviewId,
                authentication.getName()
        );

        return "redirect:/reviews/my?deleted=true";
    }
}