package com.denisar5.perfumehub.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(
            ResourceNotFoundException exception,
            Model model
    ) {
        model.addAttribute("errorTitle", "Resource Not Found");
        model.addAttribute("errorMessage", exception.getMessage());

        return "error/404";
    }

    @ExceptionHandler({
            DuplicateResourceException.class,
            InvalidOperationException.class
    })
    public String handleBusinessException(
            RuntimeException exception,
            Model model
    ) {
        model.addAttribute("errorTitle", "Action Cannot Be Completed");
        model.addAttribute("errorMessage", exception.getMessage());

        return "error/business-error";
    }

    @ExceptionHandler({
            UnauthorizedOperationException.class,
            AccessDeniedException.class
    })
    public String handleAccessDenied(
            RuntimeException exception,
            Model model
    ) {
        model.addAttribute("errorTitle", "Access Denied");
        model.addAttribute(
                "errorMessage",
                exception.getMessage() == null
                        ? "You do not have permission to perform this action."
                        : exception.getMessage()
        );

        return "error/403";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(
            IllegalArgumentException exception,
            Model model
    ) {
        model.addAttribute("errorTitle", "Invalid Request");
        model.addAttribute("errorMessage", exception.getMessage());

        return "error/business-error";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(
            Exception exception,
            Model model
    ) {
        model.addAttribute("errorTitle", "Unexpected Error");
        model.addAttribute(
                "errorMessage",
                "Something unexpected happened. Please try again."
        );

        return "error/500";
    }
}