package com.carsh.Carsh.model.repository;

import com.carsh.Carsh.model.entity.Damage;
import com.carsh.Carsh.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DamageRepository extends JpaRepository<Damage, Long> {

    List<Damage> findByOrder(Order order);

    List<Damage> findByOrderId(Long orderId);

    List<Damage> findByCarId(Long carId);
}
