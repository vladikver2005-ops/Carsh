package com.carsh.Carsh.model.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сущность Повреждение автомобиля
 */
@Entity
@Table(name = "damages")
@NoArgsConstructor
@AllArgsConstructor
public class Damage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal repairCost;

    @Column(nullable = false)
    private LocalDate damageDate = LocalDate.now();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DamageStatus status = DamageStatus.PENDING;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getRepairCost() { return repairCost; }
    public void setRepairCost(BigDecimal repairCost) { this.repairCost = repairCost; }
    
    public LocalDate getDamageDate() { return damageDate; }
    public void setDamageDate(LocalDate damageDate) { this.damageDate = damageDate; }
    
    public DamageStatus getStatus() { return status; }
    public void setStatus(DamageStatus status) { this.status = status; }

    public enum DamageStatus {
        PENDING,        // Ожидает оплаты
        PAID,           // Оплачено
        CANCELLED       // Отменено
    }
}
