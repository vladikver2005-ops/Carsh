package com.carsh.Carsh.controller;

import com.carsh.Carsh.model.entity.User;
import com.carsh.Carsh.model.service.UserService;
import com.carsh.Carsh.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
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

    @PostMapping("/auth/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       Model model,
                       RedirectAttributes redirectAttributes,
                       HttpServletResponse response) {
        try {
            // Проверяем учетные данные через AuthenticationManager
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authenticationToken);
            
            // Если аутентификация успешна, получаем пользователя
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            // Генерируем JWT токен
            String token = userService.generateToken(user);
            
            // Устанавливаем токен в cookie
            Cookie cookie = new Cookie("jwt_token", token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(24 * 60 * 60); // 24 часа
            cookie.setPath("/");
            response.addCookie(cookie);
            
            redirectAttributes.addFlashAttribute("success", "Вход выполнен успешно!");
            return "redirect:/cars";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Неверное имя пользователя или пароль");
            return "redirect:/auth/login";
        }
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
