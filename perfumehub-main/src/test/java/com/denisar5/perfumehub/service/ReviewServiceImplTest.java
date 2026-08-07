package com.denisar5.perfumehub.service;

import com.denisar5.perfumehub.client.ReviewClient;
import com.denisar5.perfumehub.client.dto.ReviewCreateRequest;
import com.denisar5.perfumehub.client.dto.ReviewResponse;
import com.denisar5.perfumehub.client.dto.ReviewUpdateRequest;
import com.denisar5.perfumehub.dto.request.ReviewCreateDto;
import com.denisar5.perfumehub.dto.request.ReviewEditDto;
import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.entity.UserEntity;
import com.denisar5.perfumehub.enums.Gender;
import com.denisar5.perfumehub.enums.UserRole;
import com.denisar5.perfumehub.exception.ResourceNotFoundException;
import com.denisar5.perfumehub.repository.PerfumeRepository;
import com.denisar5.perfumehub.repository.UserRepository;
import com.denisar5.perfumehub.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewClient reviewClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PerfumeRepository perfumeRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private UUID reviewId;
    private UUID userId;
    private UUID perfumeId;

    private UserEntity user;
    private Perfume perfume;
    private ReviewResponse reviewResponse;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        userId = UUID.randomUUID();
        perfumeId = UUID.randomUUID();

        user = new UserEntity();
        user.setId(userId);
        user.setUsername("denis");
        user.setEmail("denis@test.com");
        user.setPassword("encoded");
        user.setFirstName("Denis");
        user.setLastName("Arnaudov");
        user.setRole(UserRole.USER);
        user.setEnabled(true);

        perfume = new Perfume();
        perfume.setId(perfumeId);
        perfume.setName("Dior Sauvage");
        perfume.setBrand("Dior");
        perfume.setDescription("Fresh and spicy fragrance");
        perfume.setPrice(new BigDecimal("129.99"));
        perfume.setImageUrl("/images/dior-sauvage.jpg");
        perfume.setGender(Gender.MALE);
        perfume.setVolumeMl(100);
        perfume.setStockQuantity(10);
        perfume.setVisible(true);

        reviewResponse = new ReviewResponse();
        reviewResponse.setId(reviewId);
        reviewResponse.setPerfumeId(perfumeId);
        reviewResponse.setPerfumeName("Dior Sauvage");
        reviewResponse.setUserId(userId);
        reviewResponse.setUsername("denis");
        reviewResponse.setRating(5);
        reviewResponse.setContent(
                "Excellent fragrance with very good longevity."
        );
        reviewResponse.setApproved(false);
        reviewResponse.setCreatedAt(LocalDateTime.now());
        reviewResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getApprovedReviewsForPerfumeShouldReturnMappedReviews() {
        when(reviewClient.getApprovedReviewsForPerfume(perfumeId))
                .thenReturn(List.of(reviewResponse));

        var result =
                reviewService.getApprovedReviewsForPerfume(perfumeId);

        assertEquals(1, result.size());
        assertEquals(reviewId, result.getFirst().getId());
        assertEquals(
                "Dior Sauvage",
                result.getFirst().getPerfumeName()
        );
        assertEquals(
                "denis",
                result.getFirst().getUsername()
        );
        assertEquals(
                5,
                result.getFirst().getRating()
        );

        verify(reviewClient)
                .getApprovedReviewsForPerfume(perfumeId);
    }

    @Test
    void getReviewsForUserShouldReturnMappedReviews() {
        when(reviewClient.getReviewsForUser("denis"))
                .thenReturn(List.of(reviewResponse));

        var result =
                reviewService.getReviewsForUser("denis");

        assertEquals(1, result.size());
        assertEquals(
                reviewId,
                result.getFirst().getId()
        );
        assertEquals(
                "denis",
                result.getFirst().getUsername()
        );

        verify(reviewClient)
                .getReviewsForUser("denis");
    }

    @Test
    void getPendingReviewsShouldReturnMappedReviews() {
        when(reviewClient.getPendingReviews())
                .thenReturn(List.of(reviewResponse));

        var result =
                reviewService.getPendingReviews();

        assertEquals(1, result.size());
        assertFalse(
                result.getFirst().isApproved()
        );

        verify(reviewClient)
                .getPendingReviews();
    }

    @Test
    void createReviewShouldCallMicroservice() {
        ReviewCreateDto dto = new ReviewCreateDto();
        dto.setPerfumeId(perfumeId);
        dto.setRating(5);
        dto.setContent(
                "Excellent fragrance with very good longevity."
        );

        when(userRepository.findByUsername("denis"))
                .thenReturn(Optional.of(user));

        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.of(perfume));

        when(reviewClient.createReview(
                any(ReviewCreateRequest.class)
        )).thenReturn(reviewResponse);

        UUID result =
                reviewService.createReview("denis", dto);

        assertEquals(reviewId, result);

        ArgumentCaptor<ReviewCreateRequest> captor =
                ArgumentCaptor.forClass(
                        ReviewCreateRequest.class
                );

        verify(reviewClient)
                .createReview(captor.capture());

        ReviewCreateRequest request =
                captor.getValue();

        assertEquals(
                perfumeId,
                request.getPerfumeId()
        );

        assertEquals(
                "Dior Sauvage",
                request.getPerfumeName()
        );

        assertEquals(
                userId,
                request.getUserId()
        );

        assertEquals(
                "denis",
                request.getUsername()
        );

        assertEquals(
                5,
                request.getRating()
        );

        assertEquals(
                "Excellent fragrance with very good longevity.",
                request.getContent()
        );
    }

    @Test
    void createReviewShouldFailWhenUserNotFound() {
        ReviewCreateDto dto = new ReviewCreateDto();
        dto.setPerfumeId(perfumeId);
        dto.setRating(5);
        dto.setContent(
                "Excellent fragrance with very good longevity."
        );

        when(userRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reviewService.createReview(
                        "missing",
                        dto
                )
        );

        verify(
                reviewClient,
                never()
        ).createReview(
                any(ReviewCreateRequest.class)
        );
    }

    @Test
    void createReviewShouldFailWhenPerfumeNotFound() {
        ReviewCreateDto dto = new ReviewCreateDto();
        dto.setPerfumeId(perfumeId);
        dto.setRating(5);
        dto.setContent(
                "Excellent fragrance with very good longevity."
        );

        when(userRepository.findByUsername("denis"))
                .thenReturn(Optional.of(user));

        when(perfumeRepository.findById(perfumeId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reviewService.createReview(
                        "denis",
                        dto
                )
        );

        verify(
                reviewClient,
                never()
        ).createReview(
                any(ReviewCreateRequest.class)
        );
    }

    @Test
    void getReviewEditDtoShouldReturnReviewData() {
        when(reviewClient.getReviewsForUser("denis"))
                .thenReturn(List.of(reviewResponse));

        ReviewEditDto result =
                reviewService.getReviewEditDto(
                        reviewId,
                        "denis"
                );

        assertEquals(
                5,
                result.getRating()
        );

        assertEquals(
                "Excellent fragrance with very good longevity.",
                result.getContent()
        );
    }

    @Test
    void getReviewEditDtoShouldThrowWhenReviewDoesNotExist() {
        when(reviewClient.getReviewsForUser("denis"))
                .thenReturn(List.of());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reviewService.getReviewEditDto(
                        reviewId,
                        "denis"
                )
        );
    }

    @Test
    void editOwnReviewShouldCallMicroservice() {
        ReviewEditDto dto = new ReviewEditDto();
        dto.setRating(4);
        dto.setContent(
                "Updated review with enough content."
        );

        ReviewResponse updatedResponse =
                new ReviewResponse();

        updatedResponse.setId(reviewId);
        updatedResponse.setPerfumeId(perfumeId);
        updatedResponse.setPerfumeName(
                "Dior Sauvage"
        );
        updatedResponse.setUserId(userId);
        updatedResponse.setUsername("denis");
        updatedResponse.setRating(4);
        updatedResponse.setContent(
                "Updated review with enough content."
        );
        updatedResponse.setApproved(false);
        updatedResponse.setCreatedAt(
                LocalDateTime.now()
        );
        updatedResponse.setUpdatedAt(
                LocalDateTime.now()
        );

        when(reviewClient.updateReview(
                eq(reviewId),
                eq("denis"),
                any(ReviewUpdateRequest.class)
        )).thenReturn(updatedResponse);

        reviewService.editOwnReview(
                reviewId,
                "denis",
                dto
        );

        ArgumentCaptor<ReviewUpdateRequest> captor =
                ArgumentCaptor.forClass(
                        ReviewUpdateRequest.class
                );

        verify(reviewClient)
                .updateReview(
                        eq(reviewId),
                        eq("denis"),
                        captor.capture()
                );

        assertEquals(
                4,
                captor.getValue().getRating()
        );

        assertEquals(
                "Updated review with enough content.",
                captor.getValue().getContent()
        );
    }

    @Test
    void deleteOwnReviewShouldCallMicroservice() {
        reviewService.deleteOwnReview(
                reviewId,
                "denis"
        );

        verify(reviewClient)
                .deleteReview(
                        reviewId,
                        "denis"
                );
    }

    @Test
    void approveReviewShouldCallMicroservice() {
        when(reviewClient.approveReview(reviewId))
                .thenReturn(reviewResponse);

        reviewService.approveReview(reviewId);

        verify(reviewClient)
                .approveReview(reviewId);
    }

    @Test
    void rejectReviewShouldCallMicroservice() {
        reviewService.rejectReview(reviewId);

        verify(reviewClient)
                .rejectReview(reviewId);
    }
}