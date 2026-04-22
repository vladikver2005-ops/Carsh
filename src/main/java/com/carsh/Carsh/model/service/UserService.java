package com.carsh.Carsh.model.service;

import com.carsh.Carsh.model.entity.User;
import com.carsh.Carsh.model.repository.UserRepository;
import com.carsh.Carsh.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Регистрация нового пользователя (клиента)
     */
    public User registerClient(String username, String password, String firstName, String lastName,
                               String email, String phone) {
        
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email уже зарегистрирован");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(User.UserRole.CLIENT);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    /**
     * Создание администратора
     */
    public User createAdmin(String username, String password, String firstName, String lastName,
                            String email, String phone) {
        
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(User.UserRole.ADMIN);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    /**
     * Генерация JWT токена для пользователя
     */
    public String generateToken(User user) {
        return jwtUtil.generateToken(user);
    }

    /**
     * Получение пользователя из JWT токена
     */
    public User getUserFromToken(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
