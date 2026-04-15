package com.carsh.Carsh.controller;

import com.carsh.Carsh.model.entity.Order;
import com.carsh.Carsh.model.entity.User;
import com.carsh.Carsh.model.service.OrderService;
import com.carsh.Carsh.model.service.PaymentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public String listOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Получаем текущего пользователя и его заказы
        // В реальном приложении нужно получить User из UserRepository по username
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        model.addAttribute("order", order);
        return "orders/view";
    }

    @GetMapping("/new/{carId}")
    public String newOrderForm(@PathVariable Long carId, 
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        model.addAttribute("carId", carId);
        model.addAttribute("startDate", LocalDate.now());
        model.addAttribute("endDate", LocalDate.now().plusDays(7));
        return "orders/form";
    }

    @PostMapping("/create")
    public String createOrder(@RequestParam Long carId,
                             @RequestParam LocalDate startDate,
                             @RequestParam LocalDate endDate,
                             @RequestParam String passportSeries,
                             @RequestParam String passportNumber,
                             @RequestParam String passportIssuedBy,
                             @RequestParam LocalDate passportIssueDate,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        
        // В реальном приложении нужно получить User объект из UserRepository
        // Здесь упрощенно - предполагается что пользователь уже авторизован
        try {
            // Для демо-целей создаем заказ без привязки к конкретному пользователю
            // В продакшене нужно получить User из БД по username
            Order order = orderService.createOrder(
                null, // TODO: получить реального пользователя
                carId, 
                startDate, 
                endDate,
                passportSeries,
                passportNumber,
                passportIssuedBy,
                passportIssueDate
            );
            redirectAttributes.addFlashAttribute("success", "Заказ успешно создан!");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/new/" + carId;
        }
    }

    @GetMapping("/admin")
    public String adminOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders/admin-list";
    }

    @PostMapping("/admin/approve/{id}")
    public String approveOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.approveOrder(id);
            redirectAttributes.addFlashAttribute("success", "Заказ одобрен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/admin";
    }

    @GetMapping("/admin/reject/{id}")
    public String rejectOrderForm(@PathVariable Long id, Model model) {
        model.addAttribute("orderId", id);
        return "orders/reject-form";
    }

    @PostMapping("/admin/reject/{id}")
    public String rejectOrder(@PathVariable Long id,
                             @RequestParam String reason,
                             RedirectAttributes redirectAttributes) {
        try {
            orderService.rejectOrder(id, reason);
            redirectAttributes.addFlashAttribute("success", "Заказ отклонен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/admin";
    }

    @PostMapping("/admin/return/{id}")
    public String returnCar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.returnCar(id);
            redirectAttributes.addFlashAttribute("success", "Автомобиль возвращен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/admin";
    }
}
