package com.carsh.Carsh.model.service;

import com.carsh.Carsh.model.entity.*;
import com.carsh.Carsh.model.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public List<Car> getAvailableCars() {
        return carRepository.findByStatus(Car.CarStatus.AVAILABLE);
    }

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    public void updateCarStatus(Long carId, Car.CarStatus status) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
        car.setStatus(status);
        carRepository.save(car);
    }
}
