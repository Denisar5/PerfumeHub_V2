package com.denisar5.review_service.config;

import com.denisar5.review_service.entity.Review;
import com.denisar5.review_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ReviewRepository reviewRepository;

    @Override
    public void run(String... args) {
        if (reviewRepository.count() > 0) {
            return;
        }

        Review first = Review.builder()
                .perfumeId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .perfumeName("Sauvage Eau de Parfum")
                .userId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .username("denis")
                .rating(5)
                .content("Excellent fragrance with strong projection and very good longevity.")
                .approved(true)
                .build();

        Review second = Review.builder()
                .perfumeId(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                .perfumeName("Libre Eau de Parfum")
                .userId(UUID.fromString("44444444-4444-4444-4444-444444444444"))
                .username("alex")
                .rating(4)
                .content("Elegant floral scent with good longevity and a pleasant dry-down.")
                .approved(true)
                .build();

        Review third = Review.builder()
                .perfumeId(UUID.fromString("55555555-5555-5555-5555-555555555555"))
                .perfumeName("Oud Wood")
                .userId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .username("denis")
                .rating(5)
                .content("A refined woody fragrance that works especially well in colder weather.")
                .approved(false)
                .build();

        reviewRepository.saveAll(
                List.of(first, second, third)
        );

        log.info("Seeded review microservice data");
    }
}