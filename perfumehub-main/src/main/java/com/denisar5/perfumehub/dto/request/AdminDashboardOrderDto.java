package com.denisar5.perfumehub.dto.request;

import com.denisar5.perfumehub.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AdminDashboardOrderDto {

    private UUID id;

    private String customerUsername;

    private String perfumeName;

    private Integer quantity;

    private BigDecimal totalPrice;

    private OrderStatus status;

    private LocalDateTime createdAt;
}