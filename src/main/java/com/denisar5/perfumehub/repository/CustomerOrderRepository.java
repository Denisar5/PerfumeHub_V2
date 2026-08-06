package com.denisar5.perfumehub.repository;

import com.denisar5.perfumehub.entity.CustomerOrder;
import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.entity.UserEntity;
import com.denisar5.perfumehub.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerOrderRepository
        extends JpaRepository<CustomerOrder, UUID> {

    List<CustomerOrder> findByUserOrderByCreatedAtDesc(UserEntity user);

    List<CustomerOrder> findAllByOrderByCreatedAtDesc();

    List<CustomerOrder> findByStatus(OrderStatus status);

    boolean existsByPerfume(Perfume perfume);
}