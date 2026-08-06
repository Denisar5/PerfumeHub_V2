package com.denisar5.perfumehub.service;

import com.denisar5.perfumehub.dto.request.OrderCreateDto;
import com.denisar5.perfumehub.dto.request.OrderStatusUpdateDto;
import com.denisar5.perfumehub.dto.response.OrderViewDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    UUID createOrder(
            String username,
            OrderCreateDto orderCreateDto
    );

    void cancelOwnOrder(
            UUID orderId,
            String username
    );

    void updateOrderStatus(
            UUID orderId,
            OrderStatusUpdateDto statusUpdateDto
    );

    List<OrderViewDto> getOrdersForUser(String username);

    List<OrderViewDto> getAllOrders();
}