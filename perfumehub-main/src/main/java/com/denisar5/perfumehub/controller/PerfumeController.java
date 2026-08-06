package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.OrderCreateDto;
import com.denisar5.perfumehub.dto.request.ReviewCreateDto;
import com.denisar5.perfumehub.service.PerfumeService;
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
        prepareDetailsPage(perfumeId, model);

        if (!model.containsAttribute("orderCreateDto")) {
            OrderCreateDto orderCreateDto = new OrderCreateDto();
            orderCreateDto.setPerfumeId(perfumeId);
            orderCreateDto.setQuantity(1);

            model.addAttribute("orderCreateDto", orderCreateDto);
        }

        if (!model.containsAttribute("reviewCreateDto")) {
            ReviewCreateDto reviewCreateDto = new ReviewCreateDto();
            reviewCreateDto.setPerfumeId(perfumeId);

            model.addAttribute("reviewCreateDto", reviewCreateDto);
        }

        return "perfume/details";
    }

    public void prepareDetailsPage(
            UUID perfumeId,
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
    }
}