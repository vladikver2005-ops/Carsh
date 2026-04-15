package com.carsh.Carsh.model.repository;

import com.carsh.Carsh.model.entity.Order;
import com.carsh.Carsh.model.entity.User;
import com.carsh.Carsh.model.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClient(User client);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByClientId(Long clientId);

    List<Order> findByCarId(Long carId);
}
