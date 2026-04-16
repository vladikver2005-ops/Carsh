package com.carsh.Carsh.controller;

import com.carsh.Carsh.model.entity.User;
import com.carsh.Carsh.model.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homepage(Model model) {
        return "home";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String email,
                          @RequestParam String phone,
                          RedirectAttributes redirectAttributes) {
        try {
            userService.registerClient(username, password, firstName, lastName, email, phone);
            redirectAttributes.addFlashAttribute("success", "Регистрация успешна! Теперь вы можете войти.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/auth/login")
    public String loginForm() {
        return "auth/login";
    }

    @GetMapping("/admin/init")
    public String initAdmin() {
        // Создание администратора по умолчанию (только для демо)
        try {
            if (!userService.getUserByUsername("admin").isPresent()) {
                userService.createAdmin("admin", "admin123", "Админ", "Админов", 
                    "admin@carsh.ru", "+79990000000");
            }
        } catch (Exception e) {
            // Игнорируем если уже существует
        }
        return "redirect:/auth/login";
    }
}
