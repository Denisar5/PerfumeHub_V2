package com.denisar5.perfumehub.service;

import com.denisar5.perfumehub.dto.request.PerfumeCreateDto;
import com.denisar5.perfumehub.dto.request.PerfumeEditDto;
import com.denisar5.perfumehub.dto.request.PerfumeSearchDto;
import com.denisar5.perfumehub.dto.response.PerfumeViewDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface PerfumeService {

    UUID createPerfume(
            PerfumeCreateDto perfumeCreateDto
    );

    void editPerfume(
            UUID perfumeId,
            PerfumeEditDto perfumeEditDto
    );

    void deletePerfume(
            UUID perfumeId
    );

    void toggleVisibility(
            UUID perfumeId
    );

    PerfumeViewDto getPerfumeById(
            UUID perfumeId
    );

    PerfumeEditDto getPerfumeEditDto(
            UUID perfumeId
    );

    List<PerfumeViewDto> getVisiblePerfumes();

    List<PerfumeViewDto> getAllPerfumesForAdmin();

    Page<PerfumeViewDto> searchPerfumes(
            PerfumeSearchDto searchDto
    );

    List<String> getAvailableBrands();
}