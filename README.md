# 🎬 Movie Review System

Приложение для управления фильмами и отзывами о фильмах.

Реализовано:
- CRUD-операции для фильмов и отзывов
- Аутентификация через Basic Auth
- Кеширование GET-запросов
- Контейнеризация через Docker
- Документация API через Swagger UI

---

## 🚀 Быстрый старт

### 1. Клонирование репозитория

```bash
git clone https://github.com/Neomor/movie-review-service.git
cd movie-review-service
```

### 2. Сборка проекта
Если Maven установлен локально:
```bash
mvn clean install
```
Или используя встроенный Maven Wrapper:
```bash
./mvnw clean install
```

### 2. Запуск приложения
```bash
mvn spring-boot:run
или
./mvnw spring-boot:run
```

### 4. Доступ к Swagger UI
Перейдите в браузере:
http://localhost:8080/swagger-ui.html

## Требования

- Java 17+
- Maven 3.6+
- Docker (опционально для контейнеризации)

## Аутенфикация

Используется Basic Authentication.
- Логин и пароль указываются в application.properties.
- Для тестирования через Swagger после запуска метода нажмите Authorize и введите логин/пароль.

## Контейнеризация с Docker
Соберите jar:
```bash
mvn clean package
```
Соберите и запустите контейнер:
```bash
docker build -t movie-review-system .
docker run -p 8080:8080 movie-review-system
```
## Архитектурные решения
Структура проекта:

| Пакет | Назначение                  |
|-------|-----------------------------|
| model | JPA-сущности (Movie, Review) |
| repository | Spring Data JPA репозитории |
| service	 | Слой бизнес-логики          |
| controller | REST-контроллеры для API    |
| dto   | Data Transfer Objects (запросы/ответы) |
| mapper	 | Преобразование между Entity и DTO |
| config| Конфигурации Spring (безопасность, OpenAPI) |

Ключевые моменты:
- DTO вместо сущностей: для безопасного обмена данными через API.
- MapStruct-like маппинг вручную: для явного контроля конверсий.
- Валидация данных: через jakarta.validation аннотации.
- Swagger/OpenAPI: автоматическая генерация документации.
- Basic Auth: простая аутентификация для защиты API.
- Кэширование: используется аннотация @Cacheable для ускорения работы GET-запросов.
- Unit-тесты: тестирование сервисов с использованием Mockito и JUnit 5.
- Orphan Removal: правильно настроено каскадное удаление отзывов при удалении фильма.

## Как пользоваться API
- Создание фильма с отзывами
- Получение списка фильмов с пагинацией
- Обновление и удаление фильмов
- Создание, обновление и удаление отзывов
- Поиск лучших фильмов по жанру

Полная документация доступна в Swagger UI.

## Технологии
- Java 17
- Spring Boot 3
- Spring Data JPA
- H2 Database
- Spring Security
- Spring Cache
- Docker
- Swagger/OpenAPI
- JUnit 5 + Mockito