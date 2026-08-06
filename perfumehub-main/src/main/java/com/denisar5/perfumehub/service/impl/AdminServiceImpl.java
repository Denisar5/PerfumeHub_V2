package com.denisar5.perfumehub.service.impl;

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


    @Override
    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardData() {
        AdminDashboardDto dashboard = AdminDashboardDto.builder()
                .totalUsers(userRepository.count())
                .totalPerfumes(perfumeRepository.count())
                .totalOrders(orderRepository.count())
                .latestOrders(
                        orderRepository.findTop5ByOrderByCreatedAtDesc()
                                .stream()
                                .map(this::mapOrder)
                                .toList()
                )
                .build();

        log.info("Loaded admin dashboard statistics");

        return dashboard;
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


}