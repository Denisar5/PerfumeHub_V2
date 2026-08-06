package com.denisar5.perfumehub.client.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ReviewCreateRequest {

    private UUID perfumeId;
    private String perfumeName;
    private UUID userId;
    private String username;
    private Integer rating;
    private String content;
}