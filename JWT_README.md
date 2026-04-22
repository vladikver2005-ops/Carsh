# JWT Токен в Carsh

## Обзор

В проект добавлена поддержка JWT токенов для аутентификации пользователей и привязки заказов к пользователям.

## Что было добавлено

### 1. Зависимости (pom.xml)
Добавлены библиотеки для работы с JWT:
- `jjwt-api` - API для работы с JWT
- `jjwt-impl` - реализация JWT
- `jjwt-jackson` - поддержка JSON

### 2. JwtUtil (`src/main/java/com/carsh/Carsh/util/JwtUtil.java`)
Утилита для генерации и валидации JWT токенов.

**Методы:**
- `generateToken(User user)` - генерирует JWT токен для пользователя
- `getUserIdFromToken(String token)` - извлекает ID пользователя из токена
- `getRoleFromToken(String token)` - извлекает роль пользователя из токена
- `getUsernameFromToken(String token)` - извлекает username из токена
- `validateToken(String token, User user)` - проверяет валидность токена

**Структура JWT токена:**
```json
{
  "userId": 1,
  "role": "CLIENT",
  "username": "john_doe",
  "sub": "john_doe",
  "iat": 1234567890,
  "exp": 1234654290
}
```

### 3. JwtAuthFilter (`src/main/java/com/carsh/Carsh/config/JwtAuthFilter.java`)
Фильтр для обработки JWT токенов из:
- Cookie (имя: `jwt_token`)
- Заголовка Authorization (формат: `Bearer <token>`)

### 4. Обновленный SecurityConfig
- Добавлен JWT фильтр в цепочку безопасности
- При логауте удаляется cookie с JWT токеном

### 5. Обновленный AuthController
- Добавлен POST обработчик `/auth/login` для генерации JWT токена при входе
- Токен сохраняется в HttpOnly cookie на 24 часа

### 6. Обновленный UserService
- `generateToken(User user)` - генерация токена для пользователя
- `getUserFromToken(String token)` - получение пользователя по токену

### 7. Обновленный OrderController
- Метод `getCurrentUser()` получает пользователя из JWT токена или Spring Security контекста
- Все методы работы с заказами используют JWT для идентификации пользователя

## Как это работает

### 1. Аутентификация пользователя
```
POST /auth/login
Content-Type: application/x-www-form-urlencoded

username=user&password=pass
```

**Ответ:**
- Успех: редирект на `/cars`, установка cookie `jwt_token`
- Ошибка: редирект на `/auth/login?error=true`

### 2. Создание заказа (с JWT)
```
POST /orders/create
Cookie: jwt_token=<your_token>

carId=1&startDate=2024-01-01&endDate=2024-01-10&...
```

JWT токен автоматически извлекается из cookie, и заказ привязывается к пользователю из токена.

### 3. API использование (с заголовком)
```
GET /orders
Authorization: Bearer <your_token>
```

## Конфигурация

В `application.properties` можно настроить:
```properties
jwt.secret=вашСекретныйКлючДляПодписиТокенов
jwt.expiration=86400000  # 24 часа в миллисекундах
```

По умолчанию используются значения:
- secret: `carshSecretKeyForJwtTokenGenerationAndValidation2024`
- expiration: `86400000` (24 часа)

## Пример использования в коде

```java
// Генерация токена
String token = userService.generateToken(user);

// Получение пользователя из токена
Long userId = jwtUtil.getUserIdFromToken(token);
User user = userService.getUserById(userId).orElse(null);

// Получение роли из токена
String role = jwtUtil.getRoleFromToken(token);
```

## Безопасность

- Токены хранятся в HttpOnly cookie (недоступны для JavaScript)
- Токены подписываются алгоритмом HS512
- Время жизни токена ограничено (по умолчанию 24 часа)
- При выходе пользователя cookie с токеном удаляется
