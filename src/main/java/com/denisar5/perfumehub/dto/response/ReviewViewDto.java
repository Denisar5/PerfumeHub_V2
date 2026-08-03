package com.denisar5.perfumehub.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ReviewViewDto {

    private UUID id;

    private String username;

    private Integer rating;

    private String content;

    private boolean approved;

    private LocalDateTime createdAt;
}