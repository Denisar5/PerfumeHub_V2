package com.denisar5.perfumehub.service.impl;

import com.denisar5.perfumehub.client.ReviewClient;
import com.denisar5.perfumehub.client.dto.ReviewResponse;
import com.denisar5.perfumehub.client.dto.ReviewStatisticsResponse;
import com.denisar5.perfumehub.dto.request.AdminDashboardDto;
import com.denisar5.perfumehub.dto.request.AdminDashboardOrderDto;
import com.denisar5.perfumehub.dto.request.AdminDashboardReviewDto;
import com.denisar5.perfumehub.entity.CustomerOrder;
import com.denisar5.perfumehub.repository.CustomerOrderRepository;
import com.denisar5.perfumehub.repository.PerfumeRepository;
import com.denisar5.perfumehub.repository.UserRepository;
import com.denisar5.perfumehub.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PerfumeRepository perfumeRepository;
    private final CustomerOrderRepository orderRepository;
    private final ReviewClient reviewClient;


    @Override
    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardData() {

        ReviewStatisticsResponse statistics =
                reviewClient.getStatistics();

        return AdminDashboardDto.builder()
                .totalUsers(userRepository.count())
                .totalPerfumes(perfumeRepository.count())
                .totalOrders(orderRepository.count())
                .pendingReviews(statistics.getPendingReviews())
                .latestOrders(
                        orderRepository
                                .findTop5ByOrderByCreatedAtDesc()
                                .stream()
                                .map(this::mapOrder)
                                .toList()
                )
                .latestReviews(
                        reviewClient
                                .getLatestReviews()
                                .stream()
                                .map(this::mapReview)
                                .toList()
                )
                .build();
    }

    private AdminDashboardOrderDto mapOrder(CustomerOrder order) {
        return AdminDashboardOrderDto.builder()
                .id(order.getId())
                .customerUsername(order.getUser().getUsername())
                .perfumeName(order.getPerfume().getName())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }


    private AdminDashboardReviewDto mapReview(
            ReviewResponse review
    ) {
        return AdminDashboardReviewDto.builder()
                .id(review.getId())
                .username(review.getUsername())
                .perfumeName(review.getPerfumeName())
                .rating(review.getRating())
                .approved(review.isApproved())
                .createdAt(review.getCreatedAt())
                .build();
    }


}