package com.denisar5.perfumehub.config;

import com.denisar5.perfumehub.entity.CustomerOrder;
import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.entity.Review;
import com.denisar5.perfumehub.entity.UserEntity;
import com.denisar5.perfumehub.enums.Gender;
import com.denisar5.perfumehub.enums.OrderStatus;
import com.denisar5.perfumehub.enums.UserRole;
import com.denisar5.perfumehub.repository.CustomerOrderRepository;
import com.denisar5.perfumehub.repository.PerfumeRepository;
import com.denisar5.perfumehub.repository.ReviewRepository;
import com.denisar5.perfumehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PerfumeRepository perfumeRepository;
    private final CustomerOrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedPerfumes();
        seedReviews();
        seedOrders();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        UserEntity admin = UserEntity.builder()
                .username("admin")
                .email("admin@perfumehub.com")
                .password(passwordEncoder.encode("Admin123!"))
                .firstName("System")
                .lastName("Administrator")
                .phoneNumber("+359888111111")
                .address("Sofia, Bulgaria")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();

        UserEntity user = UserEntity.builder()
                .username("denis")
                .email("denis@perfumehub.com")
                .password(passwordEncoder.encode("User123!"))
                .firstName("Denis")
                .lastName("Arnaudov")
                .phoneNumber("+359888222222")
                .address("Sofia, Bulgaria")
                .role(UserRole.USER)
                .enabled(true)
                .build();

        UserEntity reviewer = UserEntity.builder()
                .username("alex")
                .email("alex@perfumehub.com")
                .password(passwordEncoder.encode("User123!"))
                .firstName("Alex")
                .lastName("Petrov")
                .phoneNumber("+359888333333")
                .address("Plovdiv, Bulgaria")
                .role(UserRole.USER)
                .enabled(true)
                .build();

        userRepository.saveAll(List.of(admin, user, reviewer));

        log.info("Seeded initial users");
    }

    private void seedPerfumes() {
        if (perfumeRepository.count() > 0) {
            return;
        }

        List<Perfume> perfumes = List.of(
                createPerfume(
                        "Sauvage Eau de Parfum",
                        "Dior",
                        "A fresh and powerful fragrance combining bergamot, spices and warm amber notes.",
                        "129.99",
                        "/images/dior-sauvage.jpg",
                        Gender.MALE,
                        100,
                        25
                ),

                createPerfume(
                        "Bleu de Chanel",
                        "Chanel",
                        "An elegant woody aromatic fragrance with citrus, incense and sandalwood.",
                        "139.99",
                        "/images/bleu de chanel.jpg",
                        Gender.MALE,
                        100,
                        18
                ),

                createPerfume(
                        "Libre Eau de Parfum",
                        "Yves Saint Laurent",
                        "A floral fragrance built around lavender, orange blossom and vanilla.",
                        "114.99",
                        "/images/libre.jpg",
                        Gender.FEMALE,
                        90,
                        22
                ),

                createPerfume(
                        "Black Opium",
                        "Yves Saint Laurent",
                        "A warm and addictive fragrance with coffee, vanilla and white floral notes.",
                        "119.99",
                        "/images/black-opium.jpg",
                        Gender.FEMALE,
                        90,
                        15
                ),

                createPerfume(
                        "Oud Wood",
                        "Tom Ford",
                        "A sophisticated composition of oud, sandalwood, rosewood and warm spices.",
                        "249.99",
                        "/images/oud-wood.jpg",
                        Gender.UNISEX,
                        100,
                        10
                ),

                createPerfume(
                        "Aventus",
                        "Creed",
                        "A fruity and smoky fragrance featuring pineapple, birch and musk.",
                        "319.99",
                        "/images/aventus.jpg",
                        Gender.MALE,
                        100,
                        8
                ),

                createPerfume(
                        "Baccarat Rouge 540",
                        "Maison Francis Kurkdjian",
                        "A radiant unisex fragrance with saffron, amberwood and cedar.",
                        "289.99",
                        "/images/baccarat.jpg",
                        Gender.UNISEX,
                        70,
                        12
                ),

                createPerfume(
                        "Acqua di Giò Profondo",
                        "Giorgio Armani",
                        "A marine aromatic fragrance with citrus, mineral notes and patchouli.",
                        "124.99",
                        "/images/Acqua di Giò.jpg",
                        Gender.MALE,
                        100,
                        20
                ),

                createPerfume(
                        "Coco Mademoiselle",
                        "Chanel",
                        "A bright oriental fragrance with orange, rose, patchouli and vanilla.",
                        "144.99",
                        "/images/coco.jpeg",
                        Gender.FEMALE,
                        100,
                        14
                ),

                createPerfume(
                        "Erba Pura",
                        "Xerjoff",
                        "A vibrant unisex fragrance with citrus fruits, musk, amber and vanilla.",
                        "219.99",
                        "/images/Xerjoff Erba Pura.jpg",
                        Gender.UNISEX,
                        100,
                        11
                )
        );

        perfumeRepository.saveAll(perfumes);

        log.info("Seeded initial perfumes");
    }

    private void seedReviews() {
        if (reviewRepository.count() > 0) {
            return;
        }

        UserEntity denis = userRepository.findByUsername("denis")
                .orElseThrow();

        UserEntity alex = userRepository.findByUsername("alex")
                .orElseThrow();

        List<Perfume> perfumes =
                perfumeRepository.findAllByOrderByCreatedAtDesc();

        Perfume sauvage = findPerfumeByName(
                perfumes,
                "Sauvage Eau de Parfum"
        );

        Perfume libre = findPerfumeByName(
                perfumes,
                "Libre Eau de Parfum"
        );

        Perfume oudWood = findPerfumeByName(
                perfumes,
                "Oud Wood"
        );

        Review firstReview = Review.builder()
                .user(denis)
                .perfume(sauvage)
                .rating(5)
                .content("Excellent performance and a very versatile scent for everyday use.")
                .approved(true)
                .build();

        Review secondReview = Review.builder()
                .user(alex)
                .perfume(libre)
                .rating(4)
                .content("Elegant floral fragrance with good longevity and a pleasant dry-down.")
                .approved(true)
                .build();

        Review pendingReview = Review.builder()
                .user(denis)
                .perfume(oudWood)
                .rating(5)
                .content("A refined woody fragrance that works especially well in colder weather.")
                .approved(false)
                .build();

        reviewRepository.saveAll(
                List.of(firstReview, secondReview, pendingReview)
        );

        log.info("Seeded initial reviews");
    }

    private void seedOrders() {
        if (orderRepository.count() > 0) {
            return;
        }

        UserEntity denis = userRepository.findByUsername("denis")
                .orElseThrow();

        UserEntity alex = userRepository.findByUsername("alex")
                .orElseThrow();

        List<Perfume> perfumes =
                perfumeRepository.findAllByOrderByCreatedAtDesc();

        Perfume sauvage = findPerfumeByName(
                perfumes,
                "Sauvage Eau de Parfum"
        );

        Perfume libre = findPerfumeByName(
                perfumes,
                "Libre Eau de Parfum"
        );

        CustomerOrder pendingOrder = createOrder(
                denis,
                sauvage,
                1,
                OrderStatus.PENDING,
                "Sofia, Bulgaria"
        );

        CustomerOrder completedOrder = createOrder(
                alex,
                libre,
                2,
                OrderStatus.COMPLETED,
                "Plovdiv, Bulgaria"
        );

        orderRepository.saveAll(
                List.of(pendingOrder, completedOrder)
        );

        log.info("Seeded initial orders");
    }

    private Perfume createPerfume(
            String name,
            String brand,
            String description,
            String price,
            String imageUrl,
            Gender gender,
            int volumeMl,
            int stockQuantity
    ) {
        return Perfume.builder()
                .name(name)
                .brand(brand)
                .description(description)
                .price(new BigDecimal(price))
                .imageUrl(imageUrl)
                .gender(gender)
                .volumeMl(volumeMl)
                .stockQuantity(stockQuantity)
                .visible(true)
                .build();
    }

    private CustomerOrder createOrder(
            UserEntity user,
            Perfume perfume,
            int quantity,
            OrderStatus status,
            String deliveryAddress
    ) {
        BigDecimal unitPrice = perfume.getPrice();
        BigDecimal totalPrice =
                unitPrice.multiply(BigDecimal.valueOf(quantity));

        return CustomerOrder.builder()
                .user(user)
                .perfume(perfume)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .status(status)
                .deliveryAddress(deliveryAddress)
                .build();
    }

    private Perfume findPerfumeByName(
            List<Perfume> perfumes,
            String name
    ) {
        return perfumes.stream()
                .filter(perfume -> perfume.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }
}