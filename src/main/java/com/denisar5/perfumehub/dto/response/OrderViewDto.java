package com.denisar5.perfumehub.dto.response;

import com.denisar5.perfumehub.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class OrderViewDto {

    private UUID id;

    private String perfumeName;

    private String perfumeImageUrl;

    private String customerUsername;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private OrderStatus status;

    private String deliveryAddress;

    private LocalDateTime createdAt;
}