package com.denisar5.review_service.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ApiErrorResponse {

    private int status;

    private String error;

    private String message;

    private String path;

    private LocalDateTime timestamp;

    private Map<String, String> validationErrors;
}