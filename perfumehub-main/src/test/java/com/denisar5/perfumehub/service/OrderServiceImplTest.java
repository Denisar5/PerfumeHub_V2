package com.denisar5.perfumehub.service;

import com.denisar5.perfumehub.dto.request.OrderCreateDto;
import com.denisar5.perfumehub.dto.request.OrderStatusUpdateDto;
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
import com.denisar5.perfumehub.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PerfumeRepository perfumeRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID userId;
    private UUID perfumeId;
    private UUID orderId;

    private UserEntity user;
    private Perfume perfume;
    private CustomerOrder order;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        perfumeId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        user = UserEntity.builder()
                .id(userId)
                .username("denis")
                .build();

        perfume = Perfume.builder()
                .id(perfumeId)
                .name("Dior Sauvage")
                .price(new BigDecimal("129.99"))
                .stockQuantity(10)
                .visible(true)
                .build();

        order = CustomerOrder.builder()
                .id(orderId)
                .user(user)
                .perfume(perfume)
                .quantity(2)
                .unitPrice(new BigDecimal("129.99"))
                .totalPrice(new BigDecimal("259.98"))
                .status(OrderStatus.PENDING)
                .deliveryAddress("Sofia")
                .build();
    }

    @Test
    void createOrderShouldSaveAndReduceStock() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setPerfumeId(perfumeId);
        dto.setQuantity(2);
        dto.setDeliveryAddress("Sofia");

        when(userRepository.findByUsername("denis"))
                .thenReturn(Optional.of(user));

        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.of(perfume));

        when(orderRepository.save(any(CustomerOrder.class)))
                .thenAnswer(invocation -> {
                    CustomerOrder saved = invocation.getArgument(0);
                    saved.setId(orderId);
                    return saved;
                });

        UUID result = orderService.createOrder("denis", dto);

        assertEquals(orderId, result);
        assertEquals(8, perfume.getStockQuantity());

        verify(orderRepository)
                .save(any(CustomerOrder.class));
    }

    @Test
    void createOrderShouldFailWhenStockIsInsufficient() {
        perfume.setStockQuantity(1);

        OrderCreateDto dto = new OrderCreateDto();
        dto.setPerfumeId(perfumeId);
        dto.setQuantity(2);
        dto.setDeliveryAddress("Sofia");

        when(userRepository.findByUsername("denis"))
                .thenReturn(Optional.of(user));

        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.of(perfume));

        assertThrows(
                InvalidOperationException.class,
                () -> orderService.createOrder("denis", dto)
        );

        verify(orderRepository, never())
                .save(any(CustomerOrder.class));
    }

    @Test
    void createOrderShouldFailWhenPerfumeNotFound() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setPerfumeId(perfumeId);
        dto.setQuantity(1);
        dto.setDeliveryAddress("Sofia");

        when(userRepository.findByUsername("denis"))
                .thenReturn(Optional.of(user));

        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.createOrder("denis", dto)
        );
    }

    @Test
    void cancelOwnOrderShouldCancelPendingOrderAndRestoreStock() {
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        orderService.cancelOwnOrder(orderId, "denis");

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(12, perfume.getStockQuantity());
    }

    @Test
    void cancelOwnOrderShouldFailForDifferentUser() {
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                UnauthorizedOperationException.class,
                () -> orderService.cancelOwnOrder(orderId, "someoneElse")
        );
    }

    @Test
    void cancelOwnOrderShouldFailWhenAlreadyCompleted() {
        order.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOperationException.class,
                () -> orderService.cancelOwnOrder(orderId, "denis")
        );
    }

    @Test
    void updateOrderStatusShouldChangeStatus() {
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        OrderStatusUpdateDto dto = new OrderStatusUpdateDto();
        dto.setStatus(OrderStatus.PROCESSING);

        orderService.updateOrderStatus(orderId, dto);

        assertEquals(
                OrderStatus.PROCESSING,
                order.getStatus()
        );
    }
}