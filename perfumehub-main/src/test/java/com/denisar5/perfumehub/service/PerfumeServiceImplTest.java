package com.denisar5.perfumehub.service;

import com.denisar5.perfumehub.client.ReviewClient;
import com.denisar5.perfumehub.dto.request.PerfumeCreateDto;
import com.denisar5.perfumehub.dto.request.PerfumeEditDto;
import com.denisar5.perfumehub.dto.request.PerfumeSearchDto;
import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.enums.Gender;
import com.denisar5.perfumehub.exception.InvalidOperationException;
import com.denisar5.perfumehub.exception.ResourceNotFoundException;
import com.denisar5.perfumehub.repository.CustomerOrderRepository;
import com.denisar5.perfumehub.repository.PerfumeRepository;
import com.denisar5.perfumehub.service.impl.PerfumeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerfumeServiceImplTest {

    @Mock
    private PerfumeRepository perfumeRepository;

    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private ReviewClient reviewClient;

    @InjectMocks
    private PerfumeServiceImpl perfumeService;

    private UUID perfumeId;
    private Perfume perfume;

    @BeforeEach
    void setUp() {
        perfumeId = UUID.randomUUID();

        perfume = Perfume.builder()
                .id(perfumeId)
                .name("Dior Sauvage")
                .brand("Dior")
                .description("Fresh and spicy fragrance")
                .price(new BigDecimal("129.99"))
                .imageUrl("/images/dior-sauvage.jpg")
                .gender(Gender.MALE)
                .volumeMl(100)
                .stockQuantity(10)
                .visible(true)
                .build();
    }

    @Test
    void getPerfumeByIdShouldReturnPerfume() {
        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.of(perfume));

        var result = perfumeService.getPerfumeById(perfumeId);

        assertNotNull(result);
        assertEquals("Dior Sauvage", result.getName());
        assertEquals("Dior", result.getBrand());
        assertEquals(new BigDecimal("129.99"), result.getPrice());
    }

    @Test
    void getPerfumeByIdShouldThrowWhenMissing() {
        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> perfumeService.getPerfumeById(perfumeId)
        );
    }

    @Test
    void createPerfumeShouldSaveEntity() {
        PerfumeCreateDto dto = new PerfumeCreateDto();
        dto.setName("Bleu de Chanel");
        dto.setBrand("Chanel");
        dto.setDescription("Woody aromatic fragrance");
        dto.setPrice(new BigDecimal("139.99"));
        dto.setImageUrl("/images/bleu-de-chanel.jpg");
        dto.setGender(Gender.MALE);
        dto.setVolumeMl(100);
        dto.setStockQuantity(15);
        dto.setVisible(true);

        when(perfumeRepository.save(any(Perfume.class)))
                .thenAnswer(invocation -> {
                    Perfume saved = invocation.getArgument(0);
                    saved.setId(perfumeId);
                    return saved;
                });

        UUID result = perfumeService.createPerfume(dto);

        assertEquals(perfumeId, result);

        verify(perfumeRepository)
                .save(any(Perfume.class));
    }

    @Test
    void editPerfumeShouldUpdateEntity() {
        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.of(perfume));

        PerfumeEditDto dto = new PerfumeEditDto();
        dto.setName("Updated Sauvage");
        dto.setBrand("Dior");
        dto.setDescription("Updated description");
        dto.setPrice(new BigDecimal("149.99"));
        dto.setImageUrl("/images/dior-sauvage.jpg");
        dto.setGender(Gender.MALE);
        dto.setVolumeMl(100);
        dto.setStockQuantity(8);
        dto.setVisible(true);

        perfumeService.editPerfume(perfumeId, dto);

        assertEquals("Updated Sauvage", perfume.getName());
        assertEquals(new BigDecimal("149.99"), perfume.getPrice());
        assertEquals(8, perfume.getStockQuantity());
    }

    @Test
    void deletePerfumeShouldDeleteWhenUnused() {
        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.of(perfume));

        when(orderRepository.existsByPerfume(perfume))
                .thenReturn(false);

        when(reviewClient.hasReviewsForPerfume(perfumeId))
                .thenReturn(false);

        perfumeService.deletePerfume(perfumeId);

        verify(perfumeRepository)
                .delete(perfume);
    }

    @Test
    void deletePerfumeShouldFailWhenOrdersExist() {
        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.of(perfume));

        when(orderRepository.existsByPerfume(perfume))
                .thenReturn(true);

        assertThrows(
                InvalidOperationException.class,
                () -> perfumeService.deletePerfume(perfumeId)
        );

        verify(perfumeRepository, never())
                .delete(any(Perfume.class));
    }

    @Test
    void deletePerfumeShouldFailWhenReviewsExist() {
        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.of(perfume));

        when(orderRepository.existsByPerfume(perfume))
                .thenReturn(false);

        when(reviewClient.hasReviewsForPerfume(perfumeId))
                .thenReturn(true);

        assertThrows(
                InvalidOperationException.class,
                () -> perfumeService.deletePerfume(perfumeId)
        );

        verify(perfumeRepository, never())
                .delete(any(Perfume.class));
    }

    @Test
    void toggleVisibilityShouldChangeValue() {
        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.of(perfume));

        assertTrue(perfume.isVisible());

        perfumeService.toggleVisibility(perfumeId);

        assertFalse(perfume.isVisible());
    }

    @Test
    void getAvailableBrandsShouldReturnDistinctSortedBrands() {
        Perfume chanel = Perfume.builder()
                .brand("Chanel")
                .visible(true)
                .build();

        Perfume diorSecond = Perfume.builder()
                .brand("Dior")
                .visible(true)
                .build();

        Perfume hidden = Perfume.builder()
                .brand("Tom Ford")
                .visible(false)
                .build();

        when(perfumeRepository.findAll())
                .thenReturn(List.of(
                        perfume,
                        chanel,
                        diorSecond,
                        hidden
                ));

        List<String> brands =
                perfumeService.getAvailableBrands();

        assertEquals(
                List.of("Chanel", "Dior"),
                brands
        );
    }
}