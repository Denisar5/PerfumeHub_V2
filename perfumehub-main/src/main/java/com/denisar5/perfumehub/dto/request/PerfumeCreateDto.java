package com.denisar5.perfumehub.dto.request;

import com.denisar5.perfumehub.enums.Gender;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PerfumeCreateDto {

    @NotBlank(message = "Perfume name is required")
    @Size(
            min = 2,
            max = 80,
            message = "Name must be between 2 and 80 characters"
    )
    private String name;

    @NotBlank(message = "Brand is required")
    @Size(
            min = 2,
            max = 80,
            message = "Brand must be between 2 and 80 characters"
    )
    private String brand;

    @NotBlank(message = "Description is required")
    @Size(
            min = 10,
            max = 1500,
            message = "Description must be between 10 and 1500 characters"
    )
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than zero"
    )
    private BigDecimal price;

    @NotBlank(message = "Image URL is required")
    @Size(
            max = 500,
            message = "Image URL cannot exceed 500 characters"
    )
    private String imageUrl;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Volume is required")
    @Min(value = 10, message = "Volume must be at least 10 ml")
    @Max(value = 1000, message = "Volume cannot exceed 1000 ml")
    private Integer volumeMl;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private boolean visible = true;
}