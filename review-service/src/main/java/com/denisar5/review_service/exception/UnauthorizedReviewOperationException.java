package com.denisar5.review_service.exception;

public class UnauthorizedReviewOperationException extends RuntimeException {

    public UnauthorizedReviewOperationException(String message) {
        super(message);
    }
}