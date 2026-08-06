package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.ProfileEditDto;
import com.denisar5.perfumehub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String getProfile(
            Authentication authentication,
            Model model
    ) {
        model.addAttribute(
                "user",
                userService.getUserProfile(authentication.getName())
        );

        return "profile/details";
    }

    @GetMapping("/edit")
    public String getEditProfile(
            Authentication authentication,
            Model model
    ) {
        if (!model.containsAttribute("profileEditDto")) {
            model.addAttribute(
                    "profileEditDto",
                    userService.getProfileEditDto(
                            authentication.getName()
                    )
            );
        }

        return "profile/edit";
    }

    @PostMapping("/edit")
    public String editProfile(
            @Valid @ModelAttribute("profileEditDto")
            ProfileEditDto profileEditDto,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            return "profile/edit";
        }

        userService.updateProfile(
                authentication.getName(),
                profileEditDto
        );

        return "redirect:/profile?updated=true";
    }
}