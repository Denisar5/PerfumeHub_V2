package com.denisar5.perfumehub.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
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