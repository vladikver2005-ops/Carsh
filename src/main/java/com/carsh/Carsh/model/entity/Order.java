package com.carsh.Carsh.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Сущность Заказ аренды автомобиля
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(length = 500)
    private String rejectionReason;

    @Column(nullable = false)
    private LocalDate createdAt = LocalDate.now();

    // Паспортные данные (для формы заказа)
    @Column(nullable = false)
    private String passportSeries;

    @Column(nullable = false)
    private String passportNumber;

    @Column(nullable = false)
    private String passportIssuedBy;

    @Column(nullable = false)
    private LocalDate passportIssueDate;

    public enum OrderStatus {
        PENDING,        // Ожидает подтверждения
        APPROVED,       // Подтвержден
        REJECTED,       // Отклонен
        ACTIVE,         // Активная аренда
        COMPLETED,      // Завершена
        DAMAGED         // Есть повреждения
    }
}
