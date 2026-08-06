package com.denisar5.perfumehub.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewStatisticsResponse {

    private long totalReviews;
    private long approvedReviews;
    private long pendingReviews;
}