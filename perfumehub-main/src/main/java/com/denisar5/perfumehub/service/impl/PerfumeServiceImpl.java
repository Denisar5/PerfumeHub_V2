package com.denisar5.perfumehub.service.impl;

import com.denisar5.perfumehub.client.ReviewClient;
import com.denisar5.perfumehub.dto.request.PerfumeCreateDto;
import com.denisar5.perfumehub.dto.request.PerfumeEditDto;
import com.denisar5.perfumehub.dto.request.PerfumeSearchDto;
import com.denisar5.perfumehub.dto.response.PerfumeViewDto;
import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.exception.InvalidOperationException;
import com.denisar5.perfumehub.exception.ResourceNotFoundException;
import com.denisar5.perfumehub.repository.CustomerOrderRepository;
import com.denisar5.perfumehub.repository.PerfumeRepository;
import com.denisar5.perfumehub.service.PerfumeService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerfumeServiceImpl implements PerfumeService {

    private static final int CATALOG_PAGE_SIZE = 8;

    private final PerfumeRepository perfumeRepository;
    private final CustomerOrderRepository orderRepository;
    private final ReviewClient reviewClient;

    @Override
    @Transactional
    public UUID createPerfume(PerfumeCreateDto dto) {

        Perfume perfume = Perfume.builder()
                .name(dto.getName().trim())
                .brand(dto.getBrand().trim())
                .description(dto.getDescription().trim())
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl().trim())
                .gender(dto.getGender())
                .volumeMl(dto.getVolumeMl())
                .stockQuantity(dto.getStockQuantity())
                .visible(dto.isVisible())
                .build();

        perfumeRepository.save(perfume);

        log.info(
                "Created perfume id={}, name={}, brand={}",
                perfume.getId(),
                perfume.getName(),
                perfume.getBrand()
        );

        return perfume.getId();
    }

    @Override
    @Transactional
    public void editPerfume(
            UUID perfumeId,
            PerfumeEditDto dto
    ) {

        Perfume perfume = findEntityById(perfumeId);

        perfume.setName(dto.getName().trim());
        perfume.setBrand(dto.getBrand().trim());
        perfume.setDescription(dto.getDescription().trim());
        perfume.setPrice(dto.getPrice());
        perfume.setImageUrl(dto.getImageUrl().trim());
        perfume.setGender(dto.getGender());
        perfume.setVolumeMl(dto.getVolumeMl());
        perfume.setStockQuantity(dto.getStockQuantity());
        perfume.setVisible(dto.isVisible());

        log.info(
                "Updated perfume id={}, name={}",
                perfumeId,
                perfume.getName()
        );
    }

    @Override
    @Transactional
    public void deletePerfume(UUID perfumeId) {

        Perfume perfume = findEntityById(perfumeId);

        if (orderRepository.existsByPerfume(perfume)) {
            throw new InvalidOperationException(
                    "A perfume with existing orders cannot be deleted. Hide it instead."
            );
        }

        if (reviewClient.hasReviewsForPerfume(perfumeId)) {
            throw new InvalidOperationException(
                    "A perfume with existing reviews cannot be deleted. Hide it instead."
            );
        }

        perfumeRepository.delete(perfume);

        log.info(
                "Deleted perfume id={}, name={}",
                perfumeId,
                perfume.getName()
        );
    }

    @Override
    @Transactional
    public void toggleVisibility(UUID perfumeId) {

        Perfume perfume = findEntityById(perfumeId);

        perfume.setVisible(!perfume.isVisible());

        log.info(
                "Changed visibility for perfume id={} to {}",
                perfumeId,
                perfume.isVisible()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PerfumeViewDto getPerfumeById(UUID perfumeId) {
        return mapToViewDto(
                findEntityById(perfumeId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PerfumeEditDto getPerfumeEditDto(UUID perfumeId) {

        Perfume perfume = findEntityById(perfumeId);

        PerfumeEditDto dto = new PerfumeEditDto();

        dto.setName(perfume.getName());
        dto.setBrand(perfume.getBrand());
        dto.setDescription(perfume.getDescription());
        dto.setPrice(perfume.getPrice());
        dto.setImageUrl(perfume.getImageUrl());
        dto.setGender(perfume.getGender());
        dto.setVolumeMl(perfume.getVolumeMl());
        dto.setStockQuantity(perfume.getStockQuantity());
        dto.setVisible(perfume.isVisible());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerfumeViewDto> getVisiblePerfumes() {

        return perfumeRepository
                .findByVisibleTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerfumeViewDto> getAllPerfumesForAdmin() {

        return perfumeRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PerfumeViewDto> searchPerfumes(
            PerfumeSearchDto searchDto
    ) {

        Sort sort = buildSort(
                searchDto.getSort()
        );

        int page = Math.max(
                searchDto.getPage(),
                0
        );

        Pageable pageable = PageRequest.of(
                page,
                CATALOG_PAGE_SIZE,
                sort
        );

        Specification<Perfume> specification =
                buildSpecification(searchDto);

        return perfumeRepository
                .findAll(specification, pageable)
                .map(this::mapToViewDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableBrands() {

        return perfumeRepository
                .findAll()
                .stream()
                .filter(Perfume::isVisible)
                .map(Perfume::getBrand)
                .filter(brand ->
                        brand != null
                                && !brand.isBlank()
                )
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private Specification<Perfume> buildSpecification(
            PerfumeSearchDto searchDto
    ) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates =
                    new ArrayList<>();

            // Only perfumes visible in the public catalog
            predicates.add(
                    criteriaBuilder.isTrue(
                            root.get("visible")
                    )
            );

            // Search by name OR brand
            if (searchDto.getSearch() != null
                    && !searchDto.getSearch().isBlank()) {

                String searchValue =
                        "%" +
                                searchDto
                                        .getSearch()
                                        .trim()
                                        .toLowerCase()
                                + "%";

                Predicate namePredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("name")
                                ),
                                searchValue
                        );

                Predicate brandPredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("brand")
                                ),
                                searchValue
                        );

                predicates.add(
                        criteriaBuilder.or(
                                namePredicate,
                                brandPredicate
                        )
                );
            }

            // Exact brand filter
            if (searchDto.getBrand() != null
                    && !searchDto.getBrand().isBlank()) {

                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(
                                        root.get("brand")
                                ),
                                searchDto
                                        .getBrand()
                                        .trim()
                                        .toLowerCase()
                        )
                );
            }

            // Gender filter
            if (searchDto.getGender() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("gender"),
                                searchDto.getGender()
                        )
                );
            }

            // Minimum price
            if (searchDto.getMinPrice() != null) {

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("price"),
                                searchDto.getMinPrice()
                        )
                );
            }

            // Maximum price
            if (searchDto.getMaxPrice() != null) {

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("price"),
                                searchDto.getMaxPrice()
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(
                            new Predicate[0]
                    )
            );
        };
    }

    private Sort buildSort(String sortOption) {

        if (sortOption == null
                || sortOption.isBlank()) {

            return Sort.by(
                    Sort.Direction.DESC,
                    "createdAt"
            );
        }

        return switch (sortOption) {

            case "priceAsc" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "price"
                    );

            case "priceDesc" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "price"
                    );

            case "nameAsc" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "name"
                    );

            case "nameDesc" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "name"
                    );

            case "newest" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "createdAt"
                    );

            default ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "createdAt"
                    );
        };
    }

    private Perfume findEntityById(UUID perfumeId) {

        return perfumeRepository
                .findById(perfumeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Perfume with id "
                                        + perfumeId
                                        + " was not found"
                        )
                );
    }

    private PerfumeViewDto mapToViewDto(
            Perfume perfume
    ) {

        return PerfumeViewDto.builder()
                .id(perfume.getId())
                .name(perfume.getName())
                .brand(perfume.getBrand())
                .description(perfume.getDescription())
                .price(perfume.getPrice())
                .imageUrl(perfume.getImageUrl())
                .gender(perfume.getGender())
                .volumeMl(perfume.getVolumeMl())
                .stockQuantity(perfume.getStockQuantity())
                .visible(perfume.isVisible())
                .build();
    }
}