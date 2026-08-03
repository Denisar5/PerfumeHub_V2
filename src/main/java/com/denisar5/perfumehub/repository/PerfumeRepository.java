package com.denisar5.perfumehub.repository;

import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.enums.Gender;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerfumeRepository extends JpaRepository<Perfume, UUID> {

    List<Perfume> findByVisibleTrue();

    List<Perfume> findByBrandIgnoreCase(String brand);

    List<Perfume> findByGender(Gender gender);

    List<Perfume> findByNameContainingIgnoreCase(String keyword);
}
