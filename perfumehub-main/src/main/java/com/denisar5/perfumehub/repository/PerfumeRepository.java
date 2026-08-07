package com.denisar5.perfumehub.repository;

import com.denisar5.perfumehub.entity.Perfume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerfumeRepository
        extends JpaRepository<Perfume, UUID>,
        JpaSpecificationExecutor<Perfume> {

    List<Perfume> findByVisibleTrueOrderByCreatedAtDesc();

    List<Perfume> findAllByOrderByCreatedAtDesc();

    long countByVisibleTrue();
}