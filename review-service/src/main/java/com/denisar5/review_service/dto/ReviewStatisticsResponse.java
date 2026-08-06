package com.denisar5.review_service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewStatisticsResponse {

    private long totalReviews;

    private long approvedReviews;

    private long pendingReviews;
}