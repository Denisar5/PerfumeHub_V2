package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.OrderCreateDto;
import com.denisar5.perfumehub.dto.request.PerfumeSearchDto;
import com.denisar5.perfumehub.dto.response.PerfumeViewDto;
import com.denisar5.perfumehub.dto.request.ReviewCreateDto;
import com.denisar5.perfumehub.enums.Gender;
import com.denisar5.perfumehub.service.PerfumeService;
import com.denisar5.perfumehub.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String getCatalog(
            @ModelAttribute("searchDto") PerfumeSearchDto searchDto,
            Model model
    ) {

        Page<PerfumeViewDto> perfumePage =
                perfumeService.searchPerfumes(searchDto);

        model.addAttribute(
                "perfumePage",
                perfumePage
        );

        model.addAttribute(
                "perfumes",
                perfumePage.getContent()
        );

        model.addAttribute(
                "brands",
                perfumeService.getAvailableBrands()
        );

        model.addAttribute(
                "genders",
                Gender.values()
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

        OrderCreateDto orderCreateDto =
                new OrderCreateDto();

        orderCreateDto.setPerfumeId(perfumeId);
        orderCreateDto.setQuantity(1);

        ReviewCreateDto reviewCreateDto =
                new ReviewCreateDto();

        reviewCreateDto.setPerfumeId(perfumeId);

        model.addAttribute(
                "orderCreateDto",
                orderCreateDto
        );

        model.addAttribute(
                "reviewCreateDto",
                reviewCreateDto
        );

        return "perfume/details";
    }
}