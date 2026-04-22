package com.carsh.Carsh.util;

import com.carsh.Carsh.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Утилита для работы с JWT токенами
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:carshSecretKeyForJwtTokenGenerationAndValidation2024}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    /**
     * Генерация JWT токена для пользователя
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("username", user.getUsername());
        return createToken(claims, user.getUsername());
    }

    /**
     * Создание токена с заданными claims
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Извлечение userId из токена
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Извлечение роли из токена
     */
    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    /**
     * Извлечение username из токена
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Получение даты истечения токена
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Получение конкретного claim из токена
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Получение всех claims из токена
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Проверка валидности токена
     */
    public Boolean validateToken(String token, User user) {
        final String username = getUsernameFromToken(token);
        final Long userId = getUserIdFromToken(token);
        return (username.equals(user.getUsername()) && userId.equals(user.getId()) && !isTokenExpired(token));
    }

    /**
     * Проверка истечения токена
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
