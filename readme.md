# Tic-Tac-Toe

RESTful API для игры в крестики-нолики с поддержкой:  
- Игры против компьютера (с ИИ на алгоритме minimax)  
- Мультиплеерных игр  
- JWT аутентификации  
- Истории игр и статистики игроков  
- Таблицы лидеров  
- Swagger документации  



## Запуск приложения с докером   

[//]: # (# Запускаем PostgreSQL)
[//]: # (cd src/)
[//]: # (docker-compose up -d)

[//]: # ()
[//]: # (# Проверяем что контейнер запущен)

[//]: # (docker-compose ps)

[//]: # ()
[//]: # (# Смотрим логи &#40;если нужно&#41;)

[//]: # (docker-compose logs -f postgres)


### 1. Установите Docker
`sudo apt install docker docker-compose` для Linux  
(или `sudo apt install docker.io`)  

### 2. Запустите базу данных постгрес  
`cd src`    
`docker-compose up -d`    
(Проверяем что контейнер запущен):  
`docker-compose ps`    
### 3. Запустите приложение  
`./gradlew bootRun`
## Работа с БД    
Подключение к контейнеру с PostgreSQL  
`docker exec -it tictactoe-db bash`  
Вход в Постгрес    
`psql -U game_user -d game_db`  
Просмотр таблиц  
`\dt`  
Примеры запросов  
`SELECT * FROM games;`  
`SELECT * FROM users;`

## Swagger-UI

Для проверки API можно использовать сваггер по адресу: [**http://localhost:8081/swagger-ui/index.html**](http://localhost:8081/swagger-ui/index.html)

## Тесты  
Постман коллекция (позитивные и негативные сценарии) и окружение лежат в директории `src/postman_tests`  

## Реализованные эндпоинты
**_Аутентификация_**  
`POST /auth/signup` - для регистрации пользователя  
`POST /auth/login` - для авторизации пользователя  
`POST /auth/refresh/access` - Обновление access токена  
`POST /auth/refresh` - Обновление refresh токена  
**_Игра_**    
`POST /game/new` - для создания новой игры  
`GET  /game/available` - для получения списка доступных игр  
`POST /game/{id}/join` - для присоединения к игре  
`POST /game/{gameId}` - отправляет текущую игру с обновленным игровым полем пользователем и получает в ответ:<br>- текущую игру с обновленным игровым полем компьютером при одиночной игре;<br>- или просто текущую игру при мультиплеерной игре    
`GET  /game/{id}` - для получения информации об игре  
**_Статистика_**  
`GET  /user/{user_id}/stats` - для получения информации о пользователе  
`GET  /user/me/stats` - для получения информации о текущем пользователе  
`GET /user/me/history` - История игр текущего пользователя  
`GET /user/me/stats` - Статистика текущего пользователя  
`GET /user/{userId}/stats` - Статистика любого пользователя  
`GET /leaderboard/top` - Таблица лидеров  

## Аутентификация через JWT  

1. Регистрация: POST /auth/signup
2. Логин: POST /auth/login -> получаете accessToken и refreshToken
3. Использование: В заголовке Authorization: Bearer {accessToken}
4. Обновление: POST /auth/refresh/access с refreshToken

## Архитектура проекта

> Проект разделён на логические слои. Каждый слой находится в отдельном пакете и отвечает только за свою зону ответственности.

### 1. Web слой (`web`)  
Отвечает за взаимодействие с клиентом.  

- `controller` - принимает HTTP-запросы, вызывает сервисы, возвращает ответы:  
  - AuthController.java    - Контроллер для регистрации/авторизации
  - GameController.java    - Контроллер для управления игровыми сессиями  
  - UserController.java - Контроллер для работы с пользователями и статистикой  
  - LeaderboardController.java - Контроллер для таблицы лидеров  
- `model` - модели запросов и ответов (DTO): 
  - AuthResponse.java - DTO для ответов аутентификации (успех/ошибка)  
  - CompletedGameResponse.java - DTO для информации о завершенной игре    
  - GameHistoryResponse.java - DTO для записи в истории игр  
  - GameRequest.java - DTO для запроса выполнения хода (содержит новое состояние доски)  
  - GameResponse.java - DTO для ответа с информацией об игре  
  - JwtRequest.java - DTO для запроса авторизации (логин/пароль)  
  - JwtResponse.java - DTO для ответа с JWT токенами  
  - LeaderboardEntryResponse.java - DTO для записи в таблице лидеров  
  - PlayerStatsResponse.java - DTO для статистики игрока  
  - RefreshJwtRequest.java - DTO для запроса обновления токена  
  - SignUpRequest.java - DTO для запроса регистрации пользователя  
  - UserResponse.java - DTO для информации о пользователе  
- `mapper` - преобразование web-моделей в доменные и обратно:  
  - WebGameMapper.java - маппер для преобразования между Game (domain) и GameResponse (web)  
- `filter` - валидация входящих запросов:
  - AuthFilter.java - фильтр для JWT аутентификации, извлекает токен из заголовка и устанавливает контекст безопасности  

**Web слой не содержит бизнес-логики.**

---

### 2. Domain слой (`domain`)
Содержит бизнес-логику приложения.

- `model` - доменные модели (игра, игровое поле, статус игры)
  - Game.java - основная модель игры, содержит состояние доски, игроков, статус  
  - GameStatus.java - enum статусов игры   
  - JwtAuthentication.java - модель аутентификации через JWT, наследуется от Spring Authentication  
  - Role.java - перечисление ролей пользователей (ROLE_USER)  
  - User.java - модель пользователя с данными и ролями
- `service` - интерфейсы и реализации бизнес-логики (валидация, ход компьютера, проверка окончания игры)
  - AuthService.java - интерфейс сервиса аутентификации
  - AuthServiceImpl.java - реализация аутентификации с JWT токенами
  - Constants.java - класс констант  
  - GameManagementService.java - сервис для управления играми (поиск, сохранение, фильтрация)
  - GameService.java - интерфейс игрового сервиса
  - GameServiceImpl.java - реализация игровой логики (валидация ходов, ход компьютера, проверка победителя)
  - JwtProvider.java - компонент для работы с JWT токенами (генерация, валидация, парсинг)
  - JwtUtil.java - утилита для создания JwtAuthentication из claims токена
  - StatisticsService.java - сервис для работы со статистикой и таблицей лидеров
  - UserService.java - интерфейс сервиса пользователей
  - UserServiceImpl.java - реализация сервиса пользователей


**Domain слой содержит бизнес-логику и сервисы.**  
   
---

### 3. Datasource слой (`datasource`)
Отвечает за хранение данных.

- `model` - модели хранения (entity)
  - UserEntity.java        - Сущность пользователя для JPA/Hibernate
  - GameEntity.java        - Сущность игры (уже есть)
- `repository` - интерфейсы доступа к хранилищу
  - UserRepository.java    - Репозиторий для работы с пользователями в БД
  - GameRepository.java    - Репозиторий для игр (уже есть)
- `mapper` - преобразование между domain и datasource моделями
  - GameMapper.java        - Маппер между Game и GameEntity (уже есть)
  - UserMapper.java        - Маппер между User и UserEntity

**Datasource слой не зависит от web и domain слоёв.**  
В текущей реализации используется POSTGRES БД хранилище.

---

### 4. DI слой (`di`)
Отвечает за конфигурацию зависимостей и настройку приложения.  

[//]: # (- определяет, какие реализации используются)

[//]: # (- связывает repository, service и другие компоненты)

[//]: # (- управляется Spring-контейнером)
- AppConfig.java - основная конфигурация Spring приложения
- SecurityConfig.java - конфигурация Spring Security: 
  - Настройка CORS
  - Отключение CSRF
  - Конфигурация Stateless сессий
  - Настройка доступа к endpoint'ам
  - Регистрация фильтров (AuthFilter)
- SwaggerConfig.java - конфигурация Swagger/OpenAPI документации:
  - Настройка информации об API
  - Конфигурация схемы безопасности (bearerAuth)
  - Настройка компонентов

---


## Структура проекта
```
└── tictactoe
    ├── datasource
    │   ├── mapper
    │   │   ├── GameMapper.java
    │   │   └── UserMapper.java
    │   ├── model
    │   │   ├── GameEntity.java
    │   │   └── UserEntity.java
    │   └── repository
    │       ├── GameRepository.java
    │       └── UserRepository.java
    ├── di
    │   ├── AppConfig.java
    │   ├── SecurityConfig.java
    │   └── SwaggerConfig.java
    ├── domain
    │   ├── model
    │   │   ├── Game.java
    │   │   ├── GameStatus.java
    │   │   ├── JwtAuthentication.java
    │   │   ├── Role.java
    │   │   ├── User.java
    │   │   └── UserStats.java
    │   └── service
    │       ├── AuthServiceImpl.java
    │       ├── AuthService.java
    │       ├── Constants.java
    │       ├── GameManagementService.java
    │       ├── GameServiceImpl.java
    │       ├── GameService.java
    │       ├── JwtProvider.java
    │       ├── JwtUtil.java
    │       ├── StatisticsService.java
    │       ├── UserServiceImpl.java
    │       └── UserService.java
    ├── TicTacToeApplication.java
    └── web
        ├── controller
        │   ├── AuthController.java
        │   ├── GameController.java
        │   ├── LeaderboardController.java
        │   └── UserController.java
        ├── filter
        │   └── AuthFilter.java
        ├── model
        │   ├── AuthResponse.java
        │   ├── CompletedGameResponse.java
        │   ├── GameHistoryResponse.java
        │   ├── GameRequest.java
        │   ├── GameResponse.java
        │   ├── JwtRequest.java
        │   ├── JwtResponse.java
        │   ├── LeaderboardEntryResponse.java
        │   ├── PlayerStatsResponse.java
        │   ├── RefreshJwtRequest.java
        │   ├── SignUpRequest.java
        │   └── UserResponse.java
        └── webmapper
            └── WebGameMapper.java
```

## Поток данных в приложении
```
Клиент → HTTP запрос
     |
     V
AuthFilter (JWT валидация)
     |
     V
Controller (парсинг запроса, вызов сервиса)
     |
     V
Service (бизнес-логика, проверка правил)
     |
     V
Repository (взаимодействие с БД)
     |
     V
Database (PostgreSQL)
     |
     V
Service (обработка результата)
     |
     V
Controller (формирование ответа)
     |
     V
WebMapper (преобразование в DTO)
     |
     V
Клиент (HTTP ответ)
```
## Зависимости между слоями
```

Клиент (HTTP запрос)
     |
     V
Web слой (AuthFilter → Controller)
     |
     V
Domain слой (Service → Business Logic)
     |
     V
Datasource слой (Repository → Database)
```
