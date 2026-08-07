package com.denisar5.perfumehub.service;

import com.denisar5.perfumehub.client.ReviewClient;
import com.denisar5.perfumehub.client.dto.ReviewResponse;
import com.denisar5.perfumehub.client.dto.ReviewStatisticsResponse;
import com.denisar5.perfumehub.entity.CustomerOrder;
import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.entity.UserEntity;
import com.denisar5.perfumehub.enums.OrderStatus;
import com.denisar5.perfumehub.repository.CustomerOrderRepository;
import com.denisar5.perfumehub.repository.PerfumeRepository;
import com.denisar5.perfumehub.repository.UserRepository;
import com.denisar5.perfumehub.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PerfumeRepository perfumeRepository;

    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private ReviewClient reviewClient;

    @InjectMocks
    private AdminServiceImpl adminService;

    private UserEntity user;
    private Perfume perfume;
    private CustomerOrder order;
    private ReviewResponse reviewResponse;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername("denis");

        perfume = new Perfume();
        perfume.setId(UUID.randomUUID());
        perfume.setName("Dior Sauvage");
        perfume.setPrice(new BigDecimal("129.99"));

        order = new CustomerOrder();
        order.setId(UUID.randomUUID());
        order.setUser(user);
        order.setPerfume(perfume);
        order.setQuantity(2);
        order.setUnitPrice(new BigDecimal("129.99"));
        order.setTotalPrice(new BigDecimal("259.98"));
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress("Sofia");
        order.setCreatedAt(LocalDateTime.now());

        reviewResponse = new ReviewResponse();
        reviewResponse.setId(UUID.randomUUID());
        reviewResponse.setUsername("denis");
        reviewResponse.setPerfumeName("Dior Sauvage");
        reviewResponse.setRating(5);
        reviewResponse.setApproved(false);
        reviewResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getDashboardDataShouldReturnCorrectStatistics() {
        when(userRepository.count())
                .thenReturn(5L);

        when(perfumeRepository.count())
                .thenReturn(10L);

        when(orderRepository.count())
                .thenReturn(7L);

        ReviewStatisticsResponse statistics =
                new ReviewStatisticsResponse();

        statistics.setTotalReviews(8);
        statistics.setApprovedReviews(6);
        statistics.setPendingReviews(2);

        when(reviewClient.getStatistics())
                .thenReturn(statistics);

        when(orderRepository.findTop5ByOrderByCreatedAtDesc())
                .thenReturn(List.of(order));

        when(reviewClient.getLatestReviews())
                .thenReturn(List.of(reviewResponse));

        var result =
                adminService.getDashboardData();

        assertNotNull(result);

        assertEquals(
                5,
                result.getTotalUsers()
        );

        assertEquals(
                10,
                result.getTotalPerfumes()
        );

        assertEquals(
                7,
                result.getTotalOrders()
        );

        assertEquals(
                2,
                result.getPendingReviews()
        );
    }

    @Test
    void getDashboardDataShouldReturnLatestOrders() {
        ReviewStatisticsResponse statistics =
                new ReviewStatisticsResponse();

        when(userRepository.count())
                .thenReturn(1L);

        when(perfumeRepository.count())
                .thenReturn(1L);

        when(orderRepository.count())
                .thenReturn(1L);

        when(reviewClient.getStatistics())
                .thenReturn(statistics);

        when(orderRepository.findTop5ByOrderByCreatedAtDesc())
                .thenReturn(List.of(order));

        when(reviewClient.getLatestReviews())
                .thenReturn(List.of());

        var result =
                adminService.getDashboardData();

        assertEquals(
                1,
                result.getLatestOrders().size()
        );

        var dashboardOrder =
                result.getLatestOrders().getFirst();

        assertEquals(
                "denis",
                dashboardOrder.getCustomerUsername()
        );

        assertEquals(
                "Dior Sauvage",
                dashboardOrder.getPerfumeName()
        );

        assertEquals(
                2,
                dashboardOrder.getQuantity()
        );

        assertEquals(
                new BigDecimal("259.98"),
                dashboardOrder.getTotalPrice()
        );

        assertEquals(
                OrderStatus.PENDING,
                dashboardOrder.getStatus()
        );
    }

    @Test
    void getDashboardDataShouldReturnLatestReviews() {
        ReviewStatisticsResponse statistics =
                new ReviewStatisticsResponse();

        statistics.setPendingReviews(1);

        when(userRepository.count())
                .thenReturn(1L);

        when(perfumeRepository.count())
                .thenReturn(1L);

        when(orderRepository.count())
                .thenReturn(0L);

        when(reviewClient.getStatistics())
                .thenReturn(statistics);

        when(orderRepository.findTop5ByOrderByCreatedAtDesc())
                .thenReturn(List.of());

        when(reviewClient.getLatestReviews())
                .thenReturn(List.of(reviewResponse));

        var result =
                adminService.getDashboardData();

        assertEquals(
                1,
                result.getLatestReviews().size()
        );

        var dashboardReview =
                result.getLatestReviews().getFirst();

        assertEquals(
                "denis",
                dashboardReview.getUsername()
        );

        assertEquals(
                "Dior Sauvage",
                dashboardReview.getPerfumeName()
        );

        assertEquals(
                5,
                dashboardReview.getRating()
        );

        assertFalse(
                dashboardReview.isApproved()
        );
    }

    @Test
    void getDashboardDataShouldWorkWithEmptyLists() {
        ReviewStatisticsResponse statistics =
                new ReviewStatisticsResponse();

        statistics.setTotalReviews(0);
        statistics.setApprovedReviews(0);
        statistics.setPendingReviews(0);

        when(userRepository.count())
                .thenReturn(0L);

        when(perfumeRepository.count())
                .thenReturn(0L);

        when(orderRepository.count())
                .thenReturn(0L);

        when(reviewClient.getStatistics())
                .thenReturn(statistics);

        when(orderRepository.findTop5ByOrderByCreatedAtDesc())
                .thenReturn(List.of());

        when(reviewClient.getLatestReviews())
                .thenReturn(List.of());

        var result =
                adminService.getDashboardData();

        assertNotNull(result);
        assertTrue(result.getLatestOrders().isEmpty());
        assertTrue(result.getLatestReviews().isEmpty());
        assertEquals(0, result.getPendingReviews());
    }

    @Test
    void getDashboardDataShouldCallReviewMicroservice() {
        ReviewStatisticsResponse statistics =
                new ReviewStatisticsResponse();

        when(reviewClient.getStatistics())
                .thenReturn(statistics);

        when(orderRepository.findTop5ByOrderByCreatedAtDesc())
                .thenReturn(List.of());

        when(reviewClient.getLatestReviews())
                .thenReturn(List.of());

        adminService.getDashboardData();

        verify(reviewClient, times(1))
                .getStatistics();

        verify(reviewClient, times(1))
                .getLatestReviews();
    }
}