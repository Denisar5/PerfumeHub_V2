package com.denisar5.perfumehub.repository;

import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.entity.Review;
import com.denisar5.perfumehub.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByPerfumeAndApprovedTrueOrderByCreatedAtDesc(
            Perfume perfume
    );

    List<Review> findByUserOrderByCreatedAtDesc(UserEntity user);

    List<Review> findByApprovedFalseOrderByCreatedAtAsc();

    boolean existsByUserAndPerfume(
            UserEntity user,
            Perfume perfume
    );

    boolean existsByPerfume(Perfume perfume);
}