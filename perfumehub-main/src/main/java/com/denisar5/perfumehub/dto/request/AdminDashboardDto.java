package com.denisar5.perfumehub.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminDashboardDto {

    private long totalUsers;

    private long totalPerfumes;

    private long totalOrders;

    private long pendingReviews;

    private List<AdminDashboardOrderDto> latestOrders;

    private List<AdminDashboardReviewDto> latestReviews;
}