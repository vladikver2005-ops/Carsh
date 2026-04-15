package com.carsh.Carsh.model.service;

import com.carsh.Carsh.model.entity.Order;
import com.carsh.Carsh.model.entity.Car;
import com.carsh.Carsh.model.entity.User;
import com.carsh.Carsh.model.repository.OrderRepository;
import com.carsh.Carsh.model.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CarRepository carRepository;

    public OrderService(OrderRepository orderRepository, CarRepository carRepository) {
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByClient(User client) {
        return orderRepository.findByClient(client);
    }

    public List<Order> getOrdersByClientId(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * Создание заказа на аренду автомобиля
     */
    public Order createOrder(User client, Long carId, LocalDate startDate, LocalDate endDate,
                            String passportSeries, String passportNumber, 
                            String passportIssuedBy, LocalDate passportIssueDate) {
        
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));

        if (car.getStatus() != Car.CarStatus.AVAILABLE) {
            throw new RuntimeException("Автомобиль недоступен для аренды");
        }

        // Расчет стоимости
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) {
            throw new RuntimeException("Дата окончания должна быть больше даты начала");
        }
        BigDecimal totalPrice = car.getRentalPricePerDay().multiply(BigDecimal.valueOf(days));

        Order order = new Order();
        order.setClient(client);
        order.setCar(car);
        order.setStartDate(startDate);
        order.setEndDate(endDate);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPassportSeries(passportSeries);
        order.setPassportNumber(passportNumber);
        order.setPassportIssuedBy(passportIssuedBy);
        order.setPassportIssueDate(passportIssueDate);

        return orderRepository.save(order);
    }

    /**
     * Одобрение заказа администратором
     */
    public Order approveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        
        order.setStatus(Order.OrderStatus.ACTIVE);
        order.getCar().setStatus(Car.CarStatus.RENTED);
        carRepository.save(order.getCar());
        
        return orderRepository.save(order);
    }

    /**
     * Отклонение заказа администратором
     */
    public Order rejectOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        
        order.setStatus(Order.OrderStatus.REJECTED);
        order.setRejectionReason(reason);
        
        return orderRepository.save(order);
    }

    /**
     * Возврат автомобиля
     */
    public Order returnCar(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        
        order.setStatus(Order.OrderStatus.COMPLETED);
        order.getCar().setStatus(Car.CarStatus.AVAILABLE);
        carRepository.save(order.getCar());
        
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
