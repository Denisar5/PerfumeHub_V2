package com.denisar5.perfumehub.repository;

import com.denisar5.perfumehub.entity.CustomerOrder;
import com.denisar5.perfumehub.entity.UserEntity;
import com.denisar5.perfumehub.enums.OrderStatus;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {

    List<CustomerOrder> findByUser(UserEntity user);

    List<CustomerOrder> findByStatus(OrderStatus status);
}