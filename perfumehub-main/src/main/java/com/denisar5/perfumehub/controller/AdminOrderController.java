package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.OrderStatusUpdateDto;
import com.denisar5.perfumehub.service.OrderService;
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
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String getAllOrders(Model model) {
        model.addAttribute(
                "orders",
                orderService.getAllOrders()
        );

        model.addAttribute(
                "orderStatusUpdateDto",
                new OrderStatusUpdateDto()
        );

        return "admin/orders";
    }

    @PostMapping("/{orderId}/status")
    public String updateStatus(
            @PathVariable UUID orderId,
            @Valid @ModelAttribute("orderStatusUpdateDto")
            OrderStatusUpdateDto orderStatusUpdateDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "orders",
                    orderService.getAllOrders()
            );

            return "admin/orders";
        }

        orderService.updateOrderStatus(
                orderId,
                orderStatusUpdateDto
        );

        return "redirect:/admin/orders?updated=true";
    }
}