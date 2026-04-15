package com.carsh.Carsh.model.repository;

import com.carsh.Carsh.model.entity.Car;
import com.carsh.Carsh.model.entity.Car.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByStatus(CarStatus status);

    List<Car> findByStatusIn(List<CarStatus> statuses);

    boolean existsByLicensePlate(String licensePlate);
}
