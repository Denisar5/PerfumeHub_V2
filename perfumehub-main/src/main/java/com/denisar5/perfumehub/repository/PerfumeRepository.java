package com.denisar5.perfumehub.repository;

import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerfumeRepository extends JpaRepository<Perfume, UUID> {

    List<Perfume> findByVisibleTrueOrderByCreatedAtDesc();

    List<Perfume> findAllByOrderByCreatedAtDesc();

    List<Perfume> findByBrandIgnoreCase(String brand);

    List<Perfume> findByGender(Gender gender);

    List<Perfume> findByNameContainingIgnoreCase(String keyword);
}