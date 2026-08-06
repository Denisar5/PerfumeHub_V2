package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public String getDashboard(Model model) {
        model.addAttribute(
                "dashboard",
                adminService.getDashboardData()
        );

        return "admin/dashboard";
    }
}