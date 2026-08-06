package com.denisar5.review_service.repository;

import com.denisar5.review_service.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByPerfumeIdAndApprovedTrueOrderByCreatedAtDesc(
            UUID perfumeId
    );

    List<Review> findByUsernameOrderByCreatedAtDesc(
            String username
    );

    List<Review> findByApprovedFalseOrderByCreatedAtAsc();

    List<Review> findTop5ByOrderByCreatedAtDesc();

    Optional<Review> findByIdAndUsername(
            UUID reviewId,
            String username
    );

    boolean existsByUsernameAndPerfumeId(
            String username,
            UUID perfumeId
    );

    boolean existsByPerfumeId(UUID perfumeId);

    long countByApprovedTrue();

    long countByApprovedFalse();
}