package com.carsh.Carsh.config;

import com.carsh.Carsh.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Фильтр для обработки JWT токенов
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = null;
        String username = null;

        // Извлекаем токен из cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Если токен не найден в cookie, пробуем извлечь из заголовка Authorization
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token != null) {
            try {
                username = jwtUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                logger.warn("Не удалось извлечь username из токена: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                // Создаем объект аутентификации с информацией из токена
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Добавляем атрибуты в контекст безопасности
                request.setAttribute("userId", userId);
                request.setAttribute("userRole", role);
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                logger.warn("Ошибка при установке аутентификации из JWT токена: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
