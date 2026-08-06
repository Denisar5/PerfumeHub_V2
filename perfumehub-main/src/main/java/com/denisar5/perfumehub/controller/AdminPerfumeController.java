package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.PerfumeCreateDto;
import com.denisar5.perfumehub.dto.request.PerfumeEditDto;
import com.denisar5.perfumehub.service.PerfumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/admin/perfumes")
@RequiredArgsConstructor
public class AdminPerfumeController {

    private final PerfumeService perfumeService;

    @GetMapping
    public String getAllPerfumes(Model model) {
        model.addAttribute(
                "perfumes",
                perfumeService.getAllPerfumesForAdmin()
        );

        return "admin/perfumes";
    }

    @GetMapping("/create")
    public String getCreatePage(Model model) {
        if (!model.containsAttribute("perfumeCreateDto")) {
            PerfumeCreateDto dto = new PerfumeCreateDto();
            dto.setVisible(true);

            model.addAttribute("perfumeCreateDto", dto);
        }

        return "admin/perfume-create";
    }

    @PostMapping("/create")
    public String createPerfume(
            @Valid @ModelAttribute("perfumeCreateDto")
            PerfumeCreateDto perfumeCreateDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/perfume-create";
        }

        UUID perfumeId =
                perfumeService.createPerfume(perfumeCreateDto);

        return "redirect:/perfumes/" + perfumeId
                + "?created=true";
    }

    @GetMapping("/{perfumeId}/edit")
    public String getEditPage(
            @PathVariable UUID perfumeId,
            Model model
    ) {
        if (!model.containsAttribute("perfumeEditDto")) {
            model.addAttribute(
                    "perfumeEditDto",
                    perfumeService.getPerfumeEditDto(perfumeId)
            );
        }

        model.addAttribute("perfumeId", perfumeId);

        return "admin/perfume-edit";
    }

    @PostMapping("/{perfumeId}/edit")
    public String editPerfume(
            @PathVariable UUID perfumeId,
            @Valid @ModelAttribute("perfumeEditDto")
            PerfumeEditDto perfumeEditDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("perfumeId", perfumeId);
            return "admin/perfume-edit";
        }

        perfumeService.editPerfume(
                perfumeId,
                perfumeEditDto
        );

        return "redirect:/perfumes/" + perfumeId
                + "?updated=true";
    }

    @PostMapping("/{perfumeId}/visibility")
    public String toggleVisibility(
            @PathVariable UUID perfumeId
    ) {
        perfumeService.toggleVisibility(perfumeId);

        return "redirect:/admin/perfumes";
    }

    @PostMapping("/{perfumeId}/delete")
    public String deletePerfume(
            @PathVariable UUID perfumeId
    ) {
        perfumeService.deletePerfume(perfumeId);

        return "redirect:/admin/perfumes?deleted=true";
    }
}