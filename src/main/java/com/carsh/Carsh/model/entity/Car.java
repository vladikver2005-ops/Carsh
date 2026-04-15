package com.carsh.Carsh.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Сущность Автомобиль
 */
@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private BigDecimal rentalPricePerDay;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CarStatus status = CarStatus.AVAILABLE;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String imageUrl;

    public enum CarStatus {
        AVAILABLE,      // Доступен для аренды
        RENTED,         // В аренде
        MAINTENANCE,    // На обслуживании
        DAMAGED         // Поврежден
    }
}
