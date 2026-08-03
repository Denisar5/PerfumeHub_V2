package com.denisar5.perfumehub.entity;

import com.denisar5.perfumehub.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "perfumes")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Perfume extends BaseEntity {

    @NotBlank(message = "Perfume name is required")
    @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
    @Column(nullable = false, length = 80)
    private String name;

    @NotBlank(message = "Brand is required")
    @Size(min = 2, max = 80, message = "Brand must be between 2 and 80 characters")
    @Column(nullable = false, length = 80)
    private String brand;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1500, message = "Description must be between 10 and 1500 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank(message = "Image URL is required")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String imageUrl;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @NotNull(message = "Volume is required")
    @Min(value = 10, message = "Volume must be at least 10 ml")
    @Max(value = 1000, message = "Volume cannot exceed 1000 ml")
    @Column(nullable = false)
    private Integer volumeMl;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private boolean visible;
}