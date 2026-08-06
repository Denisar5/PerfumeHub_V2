package com.denisar5.perfumehub.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AdminDashboardReviewDto {

    private UUID id;

    private String username;

    private String perfumeName;

    private Integer rating;

    private boolean approved;

    private LocalDateTime createdAt;
}