package com.carsh.Carsh.model.service;

import com.carsh.Carsh.model.entity.Damage;
import com.carsh.Carsh.model.entity.Order;
import com.carsh.Carsh.model.entity.Car;
import com.carsh.Carsh.model.repository.DamageRepository;
import com.carsh.Carsh.model.repository.OrderRepository;
import com.carsh.Carsh.model.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DamageService {

    private final DamageRepository damageRepository;
    private final OrderRepository orderRepository;
    private final CarRepository carRepository;

    public DamageService(DamageRepository damageRepository, OrderRepository orderRepository, CarRepository carRepository) {
        this.damageRepository = damageRepository;
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
    }

    public List<Damage> getAllDamages() {
        return damageRepository.findAll();
    }

    public List<Damage> getDamagesByOrderId(Long orderId) {
        return damageRepository.findByOrderId(orderId);
    }

    public List<Damage> getDamagesByCarId(Long carId) {
        return damageRepository.findByCarId(carId);
    }

    public Optional<Damage> getDamageById(Long id) {
        return damageRepository.findById(id);
    }

    public Damage saveDamage(Damage damage) {
        return damageRepository.save(damage);
    }

    /**
     * Регистрация повреждения при возврате автомобиля
     */
    public Damage createDamage(Long orderId, String description, BigDecimal repairCost) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        Damage damage = new Damage();
        damage.setOrder(order);
        damage.setCar(order.getCar());
        damage.setDescription(description);
        damage.setRepairCost(repairCost);
        damage.setStatus(Damage.DamageStatus.PENDING);

        // Обновляем статус заказа и автомобиля
        order.setStatus(Order.OrderStatus.DAMAGED);
        order.getCar().setStatus(Car.CarStatus.DAMAGED);
        orderRepository.save(order);
        carRepository.save(order.getCar());

        return damageRepository.save(damage);
    }

    /**
     * Оплата ремонта
     */
    public Damage payDamage(Long damageId) {
        Damage damage = damageRepository.findById(damageId)
                .orElseThrow(() -> new RuntimeException("Повреждение не найдено"));

        damage.setStatus(Damage.DamageStatus.PAID);
        
        // После оплаты автомобиль снова доступен
        damage.getCar().setStatus(Car.CarStatus.AVAILABLE);
        carRepository.save(damage.getCar());

        return damageRepository.save(damage);
    }

    public void deleteDamage(Long id) {
        damageRepository.deleteById(id);
    }
}
