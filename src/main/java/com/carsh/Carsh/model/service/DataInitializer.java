package com.carsh.Carsh.model.service;

import com.carsh.Carsh.model.entity.Car;
import com.carsh.Carsh.model.entity.User;
import com.carsh.Carsh.model.repository.CarRepository;
import com.carsh.Carsh.model.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CarRepository carRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Создаем администратора по умолчанию
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFirstName("Админ");
                admin.setLastName("Админов");
                admin.setEmail("admin@carsh.ru");
                admin.setPhone("+79990000000");
                admin.setRole(User.UserRole.ADMIN);
                admin.setEnabled(true);
                userRepository.save(admin);
            }

            // Создаем тестового клиента
            if (!userRepository.existsByUsername("client")) {
                User client = new User();
                client.setUsername("client");
                client.setPassword(passwordEncoder.encode("client123"));
                client.setFirstName("Иван");
                client.setLastName("Клиентов");
                client.setEmail("client@example.com");
                client.setPhone("+79991112233");
                client.setRole(User.UserRole.CLIENT);
                client.setEnabled(true);
                userRepository.save(client);
            }

            // Создаем тестовые автомобили
            if (carRepository.count() == 0) {
                Car car1 = new Car();
                car1.setBrand("Toyota");
                car1.setModel("Camry");
                car1.setYear(2022);
                car1.setColor("Черный");
                car1.setLicensePlate("А123АА777");
                car1.setRentalPricePerDay(new BigDecimal("5000.00"));
                car1.setStatus(Car.CarStatus.AVAILABLE);
                car1.setDescription("Комфортный седан бизнес-класса");
                carRepository.save(car1);

                Car car2 = new Car();
                car2.setBrand("BMW");
                car2.setModel("X5");
                car2.setYear(2023);
                car2.setColor("Белый");
                car2.setLicensePlate("В456ВВ777");
                car2.setRentalPricePerDay(new BigDecimal("12000.00"));
                car2.setStatus(Car.CarStatus.AVAILABLE);
                car2.setDescription("Премиальный внедорожник");
                carRepository.save(car2);

                Car car3 = new Car();
                car3.setBrand("Hyundai");
                car3.setModel("Solaris");
                car3.setYear(2021);
                car3.setColor("Серебристый");
                car3.setLicensePlate("С789СС777");
                car3.setRentalPricePerDay(new BigDecimal("3000.00"));
                car3.setStatus(Car.CarStatus.AVAILABLE);
                car3.setDescription("Экономичный автомобиль для города");
                carRepository.save(car3);

                Car car4 = new Car();
                car4.setBrand("Mercedes-Benz");
                car4.setModel("E-Class");
                car4.setYear(2023);
                car4.setColor("Черный");
                car4.setLicensePlate("Е010ЕЕ777");
                car4.setRentalPricePerDay(new BigDecimal("10000.00"));
                car4.setStatus(Car.CarStatus.AVAILABLE);
                car4.setDescription("Люксовый седан");
                carRepository.save(car4);
            }
        };
    }
}
