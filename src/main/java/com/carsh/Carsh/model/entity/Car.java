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
    private Integer carYear;

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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getCarYear() { return carYear; }
    public void setYear(int year) { this.carYear = year; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    
    public BigDecimal getRentalPricePerDay() { return rentalPricePerDay; }
    public void setRentalPricePerDay(BigDecimal rentalPricePerDay) { this.rentalPricePerDay = rentalPricePerDay; }
    
    public CarStatus getStatus() { return status; }
    public void setStatus(CarStatus status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public enum CarStatus {
        AVAILABLE,      // Доступен для аренды
        RENTED,         // В аренде
        MAINTENANCE,    // На обслуживании
        DAMAGED         // Поврежден
    }
}
