package com.denisar5.perfumehub.service.impl;

import com.denisar5.perfumehub.dto.request.OrderCreateDto;
import com.denisar5.perfumehub.dto.request.OrderStatusUpdateDto;
import com.denisar5.perfumehub.dto.response.OrderViewDto;
import com.denisar5.perfumehub.entity.CustomerOrder;
import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.entity.UserEntity;
import com.denisar5.perfumehub.enums.OrderStatus;
import com.denisar5.perfumehub.exception.InvalidOperationException;
import com.denisar5.perfumehub.exception.ResourceNotFoundException;
import com.denisar5.perfumehub.exception.UnauthorizedOperationException;
import com.denisar5.perfumehub.repository.CustomerOrderRepository;
import com.denisar5.perfumehub.repository.PerfumeRepository;
import com.denisar5.perfumehub.repository.UserRepository;
import com.denisar5.perfumehub.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CustomerOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PerfumeRepository perfumeRepository;

    @Override
    @Transactional
    public UUID createOrder(
            String username,
            OrderCreateDto dto
    ) {
        UserEntity user = findUserByUsername(username);
        Perfume perfume = findPerfumeById(dto.getPerfumeId());

        if (!perfume.isVisible()) {
            throw new InvalidOperationException(
                    "This perfume is not currently available"
            );
        }

        if (perfume.getStockQuantity() < dto.getQuantity()) {
            throw new InvalidOperationException(
                    "Not enough perfume units are available"
            );
        }

        BigDecimal totalPrice = perfume.getPrice()
                .multiply(BigDecimal.valueOf(dto.getQuantity()));

        CustomerOrder order = CustomerOrder.builder()
                .user(user)
                .perfume(perfume)
                .quantity(dto.getQuantity())
                .unitPrice(perfume.getPrice())
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .deliveryAddress(dto.getDeliveryAddress().trim())
                .build();

        perfume.setStockQuantity(
                perfume.getStockQuantity() - dto.getQuantity()
        );

        orderRepository.save(order);

        log.info(
                "User username={} created order id={} for perfume id={}",
                username,
                order.getId(),
                perfume.getId()
        );

        return order.getId();
    }

    @Override
    @Transactional
    public void cancelOwnOrder(
            UUID orderId,
            String username
    ) {
        CustomerOrder order = findOrderById(orderId);

        if (!order.getUser().getUsername().equals(username)) {
            throw new UnauthorizedOperationException(
                    "You cannot cancel another user's order"
            );
        }

        cancelOrder(order);

        log.info(
                "User username={} cancelled order id={}",
                username,
                orderId
        );
    }

    @Override
    @Transactional
    public void updateOrderStatus(
            UUID orderId,
            OrderStatusUpdateDto dto
    ) {
        CustomerOrder order = findOrderById(orderId);
        OrderStatus newStatus = dto.getStatus();

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOperationException(
                    "A cancelled order cannot be updated"
            );
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new InvalidOperationException(
                    "A completed order cannot be updated"
            );
        }

        if (newStatus == OrderStatus.CANCELLED) {
            cancelOrder(order);
        } else {
            order.setStatus(newStatus);
        }

        log.info(
                "Updated order id={} status to {}",
                orderId,
                newStatus
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderViewDto> getOrdersForUser(String username) {
        UserEntity user = findUserByUsername(username);

        return orderRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderViewDto> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    private void cancelOrder(CustomerOrder order) {
        if (order.getStatus() != OrderStatus.PENDING
                && order.getStatus() != OrderStatus.PROCESSING) {

            throw new InvalidOperationException(
                    "Only pending or processing orders can be cancelled"
            );
        }

        Perfume perfume = order.getPerfume();

        perfume.setStockQuantity(
                perfume.getStockQuantity() + order.getQuantity()
        );

        order.setStatus(OrderStatus.CANCELLED);
    }

    private CustomerOrder findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found"
                ));
    }

    private UserEntity findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));
    }

    private Perfume findPerfumeById(UUID perfumeId) {
        return perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfume not found"
                ));
    }

    private OrderViewDto mapToViewDto(CustomerOrder order) {
        return OrderViewDto.builder()
                .id(order.getId())
                .perfumeName(order.getPerfume().getName())
                .perfumeImageUrl(order.getPerfume().getImageUrl())
                .customerUsername(order.getUser().getUsername())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .deliveryAddress(order.getDeliveryAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }
}