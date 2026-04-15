package com.carsh.Carsh.controller;

import com.carsh.Carsh.model.entity.Car;
import com.carsh.Carsh.model.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public String listCars(Model model) {
        model.addAttribute("cars", carService.getAvailableCars());
        return "cars/list";
    }

    @GetMapping("/{id}")
    public String viewCar(@PathVariable Long id, Model model) {
        Car car = carService.getCarById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
        model.addAttribute("car", car);
        return "cars/view";
    }

    @GetMapping("/admin")
    public String adminCars(Model model) {
        model.addAttribute("cars", carService.getAllCars());
        return "cars/admin-list";
    }

    @GetMapping("/admin/new")
    public String newCarForm(Model model) {
        model.addAttribute("car", new Car());
        return "cars/form";
    }

    @PostMapping("/admin/save")
    public String saveCar(@ModelAttribute Car car) {
        carService.saveCar(car);
        return "redirect:/cars/admin";
    }

    @GetMapping("/admin/edit/{id}")
    public String editCarForm(@PathVariable Long id, Model model) {
        Car car = carService.getCarById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
        model.addAttribute("car", car);
        return "cars/form";
    }

    @PostMapping("/admin/update")
    public String updateCar(@ModelAttribute Car car) {
        carService.saveCar(car);
        return "redirect:/cars/admin";
    }

    @PostMapping("/admin/delete/{id}")
    public String deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return "redirect:/cars/admin";
    }
}
