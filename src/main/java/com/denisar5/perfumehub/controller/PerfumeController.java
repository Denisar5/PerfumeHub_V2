package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.OrderCreateDto;
import com.denisar5.perfumehub.dto.request.ReviewCreateDto;
import com.denisar5.perfumehub.service.PerfumeService;
import com.denisar5.perfumehub.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/perfumes")
@RequiredArgsConstructor
public class PerfumeController {

    private final PerfumeService perfumeService;
    private final ReviewService reviewService;

    @GetMapping
    public String getCatalog(Model model) {
        model.addAttribute(
                "perfumes",
                perfumeService.getVisiblePerfumes()
        );

        return "perfume/catalog";
    }

    @GetMapping("/{perfumeId}")
    public String getDetails(
            @PathVariable UUID perfumeId,
            Model model
    ) {
        model.addAttribute(
                "perfume",
                perfumeService.getPerfumeById(perfumeId)
        );

        model.addAttribute(
                "reviews",
                reviewService.getApprovedReviewsForPerfume(perfumeId)
        );

        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setPerfumeId(perfumeId);
        orderCreateDto.setQuantity(1);

        ReviewCreateDto reviewCreateDto = new ReviewCreateDto();
        reviewCreateDto.setPerfumeId(perfumeId);

        model.addAttribute("orderCreateDto", orderCreateDto);
        model.addAttribute("reviewCreateDto", reviewCreateDto);

        return "perfume/details";
    }
}