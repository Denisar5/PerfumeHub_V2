package com.denisar5.perfumehub.service.impl;

import com.denisar5.perfumehub.dto.request.PerfumeCreateDto;
import com.denisar5.perfumehub.dto.request.PerfumeEditDto;
import com.denisar5.perfumehub.dto.response.PerfumeViewDto;
import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.exception.InvalidOperationException;
import com.denisar5.perfumehub.exception.ResourceNotFoundException;
import com.denisar5.perfumehub.repository.CustomerOrderRepository;
import com.denisar5.perfumehub.repository.PerfumeRepository;
import com.denisar5.perfumehub.service.PerfumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerfumeServiceImpl implements PerfumeService {

    private final PerfumeRepository perfumeRepository;
    private final CustomerOrderRepository orderRepository;

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
                "Created perfume id={}, name={}",
                perfume.getId(),
                perfume.getName()
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
                perfume.getId(),
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
        return mapToViewDto(findEntityById(perfumeId));
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
        return perfumeRepository.findByVisibleTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerfumeViewDto> getAllPerfumesForAdmin() {
        return perfumeRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    private Perfume findEntityById(UUID perfumeId) {
        return perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfume not found"
                ));
    }

    private PerfumeViewDto mapToViewDto(Perfume perfume) {
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