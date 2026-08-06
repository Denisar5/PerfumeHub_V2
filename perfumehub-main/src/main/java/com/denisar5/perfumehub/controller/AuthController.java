package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.RegisterDto;
import com.denisar5.perfumehub.exception.DuplicateResourceException;
import com.denisar5.perfumehub.exception.InvalidOperationException;
import com.denisar5.perfumehub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String getLoginPage(Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String getRegisterPage(
            Model model,
            Authentication authentication
    ) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }

        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new RegisterDto());
        }

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerDto")
            RegisterDto registerDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.register(registerDto);
        } catch (DuplicateResourceException |
                 InvalidOperationException exception) {

            bindingResult.reject(
                    "registration.error",
                    exception.getMessage()
            );

            return "auth/register";
        }

        return "redirect:/login?registered=true";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}