package com.denisar5.review_service.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ReviewResponse {

    private UUID id;

    private UUID perfumeId;

    private String perfumeName;

    private UUID userId;

    private String username;

    private Integer rating;

    private String content;

    private boolean approved;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}