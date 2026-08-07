package com.denisar5.perfumehub.dto.request;

import com.denisar5.perfumehub.enums.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PerfumeSearchDto {

    private String search;

    private String brand;

    private Gender gender;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String sort = "newest";

    private int page = 0;
}