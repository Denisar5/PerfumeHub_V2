package com.denisar5.perfumehub.repository;

import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.entity.Review;
import com.denisar5.perfumehub.entity.UserEntity;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByPerfumeAndApprovedTrue(Perfume perfume);

    List<Review> findByUser(UserEntity user);

    boolean existsByUserAndPerfume(UserEntity user, Perfume perfume);
}
