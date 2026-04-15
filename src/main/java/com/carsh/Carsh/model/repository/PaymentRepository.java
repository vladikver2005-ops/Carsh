package com.carsh.Carsh.model.repository;

import com.carsh.Carsh.model.entity.Payment;
import com.carsh.Carsh.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrder(Order order);

    List<Payment> findByOrderId(Long orderId);
}
