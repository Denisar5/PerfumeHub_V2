package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.RoleUpdateDto;
import com.denisar5.perfumehub.service.UserService;
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
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String getUsers(Model model) {
        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        model.addAttribute(
                "roleUpdateDto",
                new RoleUpdateDto()
        );

        return "admin/users";
    }

    @PostMapping("/{userId}/role")
    public String updateRole(
            @PathVariable UUID userId,
            @Valid @ModelAttribute("roleUpdateDto")
            RoleUpdateDto roleUpdateDto,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "users",
                    userService.getAllUsers()
            );

            return "admin/users";
        }

        userService.updateUserRole(
                userId,
                roleUpdateDto,
                authentication.getName()
        );

        return "redirect:/admin/users?roleUpdated=true";
    }

    @PostMapping("/{userId}/enabled")
    public String toggleEnabled(
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        userService.changeUserEnabledStatus(
                userId,
                authentication.getName()
        );

        return "redirect:/admin/users?statusUpdated=true";
    }
}