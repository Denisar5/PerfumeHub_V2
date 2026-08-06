package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.OrderCreateDto;
import com.denisar5.perfumehub.service.OrderService;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my")
    public String getMyOrders(
            Authentication authentication,
            Model model
    ) {
        model.addAttribute(
                "orders",
                orderService.getOrdersForUser(
                        authentication.getName()
                )
        );

        return "order/my-orders";
    }

    @PostMapping
    public String createOrder(
            @Valid @ModelAttribute("orderCreateDto")
            OrderCreateDto orderCreateDto,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return returnToPerfumeDetails(
                    orderCreateDto.getPerfumeId(),
                    model
            );
        }

        orderService.createOrder(
                authentication.getName(),
                orderCreateDto
        );

        return "redirect:/orders/my?created=true";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(
            @PathVariable UUID orderId,
            Authentication authentication
    ) {
        orderService.cancelOwnOrder(
                orderId,
                authentication.getName()
        );

        return "redirect:/orders/my?cancelled=true";
    }

    private String returnToPerfumeDetails(
            UUID perfumeId,
            Model model
    ) {
        model.addAttribute("perfumeId", perfumeId);

        return "redirect:/perfumes/" + perfumeId;
    }
}