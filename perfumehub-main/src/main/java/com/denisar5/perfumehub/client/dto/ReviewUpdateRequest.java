package com.denisar5.perfumehub.client.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewUpdateRequest {

    private Integer rating;
    private String content;
}