package com.carsh.Carsh.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сущность Повреждение автомобиля
 */
@Entity
@Table(name = "damages")
@Data
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

    public enum DamageStatus {
        PENDING,        // Ожидает оплаты
        PAID,           // Оплачено
        CANCELLED       // Отменено
    }
}
