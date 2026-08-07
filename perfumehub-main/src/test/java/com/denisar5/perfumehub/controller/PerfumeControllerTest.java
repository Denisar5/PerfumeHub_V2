package com.denisar5.perfumehub.controller;

import com.denisar5.perfumehub.dto.request.PerfumeSearchDto;
import com.denisar5.perfumehub.dto.response.PerfumeViewDto;
import com.denisar5.perfumehub.enums.Gender;
import com.denisar5.perfumehub.service.PerfumeService;
import com.denisar5.perfumehub.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PerfumeController.class)
@AutoConfigureMockMvc(addFilters = false)
class PerfumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PerfumeService perfumeService;

    @MockitoBean
    private ReviewService reviewService;

    @Test
    void getCatalogShouldReturnCatalogPage() throws Exception {

        PerfumeViewDto perfume =
                createPerfume(UUID.randomUUID());

        when(perfumeService.searchPerfumes(
                any(PerfumeSearchDto.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(perfume)
                )
        );

        when(perfumeService.getAvailableBrands())
                .thenReturn(
                        List.of(
                                "Chanel",
                                "Dior"
                        )
                );

        mockMvc.perform(
                        get("/perfumes")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name(
                                "perfume/catalog"
                        )
                )
                .andExpect(
                        model().attributeExists(
                                "perfumePage"
                        )
                )
                .andExpect(
                        model().attributeExists(
                                "perfumes"
                        )
                )
                .andExpect(
                        model().attributeExists(
                                "brands"
                        )
                )
                .andExpect(
                        model().attributeExists(
                                "genders"
                        )
                )
                .andExpect(
                        model().attributeExists(
                                "searchDto"
                        )
                );

        verify(perfumeService)
                .searchPerfumes(
                        any(PerfumeSearchDto.class)
                );

        verify(perfumeService)
                .getAvailableBrands();
    }

    @Test
    void getCatalogShouldAcceptSearchParameters() throws Exception {

        when(perfumeService.searchPerfumes(
                any(PerfumeSearchDto.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of()
                )
        );

        when(perfumeService.getAvailableBrands())
                .thenReturn(
                        List.of("Dior")
                );

        mockMvc.perform(
                        get("/perfumes")
                                .param(
                                        "search",
                                        "sauvage"
                                )
                                .param(
                                        "brand",
                                        "Dior"
                                )
                                .param(
                                        "gender",
                                        "MALE"
                                )
                                .param(
                                        "minPrice",
                                        "100"
                                )
                                .param(
                                        "maxPrice",
                                        "200"
                                )
                                .param(
                                        "sort",
                                        "priceAsc"
                                )
                                .param(
                                        "page",
                                        "0"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name(
                                "perfume/catalog"
                        )
                )
                .andExpect(
                        model().attributeExists(
                                "searchDto"
                        )
                )
                .andExpect(
                        model().attributeExists(
                                "perfumePage"
                        )
                )
                .andExpect(
                        model().attributeExists(
                                "perfumes"
                        )
                );
    }

    @Test
    void getCatalogShouldReturnEmptyCatalog() throws Exception {

        when(perfumeService.searchPerfumes(
                any(PerfumeSearchDto.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of()
                )
        );

        when(perfumeService.getAvailableBrands())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/perfumes")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name(
                                "perfume/catalog"
                        )
                )
                .andExpect(
                        model().attribute(
                                "perfumes",
                                List.of()
                        )
                );
    }

    @Test
    void getDetailsShouldReturnPerfumeDetailsPage()
            throws Exception {

        UUID perfumeId =
                UUID.randomUUID();

        PerfumeViewDto perfume =
                createPerfume(perfumeId);

        when(perfumeService.getPerfumeById(
                perfumeId
        )).thenReturn(perfume);

        when(reviewService
                .getApprovedReviewsForPerfume(
                        perfumeId
                ))
                .thenReturn(List.of());

        mockMvc.perform(
                        get(
                                "/perfumes/{perfumeId}",
                                perfumeId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name(
                                "perfume/details"
                        )
                )
                .andExpect(
                        model().attribute(
                                "perfume",
                                perfume
                        )
                )
                .andExpect(
                        model().attributeExists(
                                "reviews"
                        )
                )
                .andExpect(
                        model().attributeExists(
                                "orderCreateDto"
                        )
                )
                .andExpect(
                        model().attributeExists(
                                "reviewCreateDto"
                        )
                );

        verify(perfumeService)
                .getPerfumeById(
                        perfumeId
                );

        verify(reviewService)
                .getApprovedReviewsForPerfume(
                        perfumeId
                );
    }

    private PerfumeViewDto createPerfume(
            UUID perfumeId
    ) {

        return PerfumeViewDto.builder()
                .id(perfumeId)
                .name("Dior Sauvage")
                .brand("Dior")
                .description(
                        "Fresh and spicy fragrance"
                )
                .price(
                        new BigDecimal(
                                "129.99"
                        )
                )
                .imageUrl(
                        "/images/dior-sauvage.jpg"
                )
                .gender(
                        Gender.MALE
                )
                .volumeMl(100)
                .stockQuantity(10)
                .visible(true)
                .build();
    }
}