package com.denisar5.perfumehub.dto.response;

import com.denisar5.perfumehub.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class PerfumeViewDto {

    private UUID id;

    private String name;

    private String brand;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    private Gender gender;

    private Integer volumeMl;

    private Integer stockQuantity;

    private boolean visible;
}