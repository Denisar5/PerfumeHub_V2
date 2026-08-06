package com.denisar5.perfumehub.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderCreateDto {

    @NotNull(message = "Perfume is required")
    private UUID perfumeId;

    @NotNull(message = "Quantity is required")
    @Min(
            value = 1,
            message = "Quantity must be at least 1"
    )
    private Integer quantity;

    @NotBlank(message = "Delivery address is required")
    @Size(
            min = 5,
            max = 255,
            message = "Delivery address must be between 5 and 255 characters"
    )
    private String deliveryAddress;
}