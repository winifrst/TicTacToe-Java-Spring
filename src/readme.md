# Tic-Tac-Toe

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

[//]: # (Приложение будет доступно по адресу: [http://localhost:8080]&#40;http://localhost:8080&#41;)

## Работа с БД    
`docker exec -it tictactoe-db bash`  
`psql -U game_user -d game_db`  
`\dt`  
`SELECT * FROM games;`  
`SELECT * FROM users;`

## Swagger-UI  
Для проверки API можно использовать сваггер по адресу: [**http://localhost:8080/swagger-ui/index.html#**](http://localhost:8080/swagger-ui/index.html#)  

## Тесты  
Постман коллекция (позитивные и негативные сценарии) и окружение лежат в директории `src/postman_tests`  

## Реализованные эндпоинты
`POST /auth/signup` - для регистрации пользователя  
`POST /auth/login` - для авторизации пользователя  
`POST /game/new` - для создания новой игры  
`GET  /game/available` - для получения списка доступных игр  
`POST /game/{id}/join` - для присоединения к игре  
`POST /game/{gameId}` - отправляет текущую игру с обновленным игровым полем пользователем и получает в ответ:<br>- текущую игру с обновленным игровым полем компьютером при одиночной игре;<br>- или просто текущую игру при мультиплеерной игре    
`GET  /game/{id}` - для получения информации об игре  
`GET  /user/{id}` - для получения информации о пользователе  
`GET  /user/me` - для получения информации о текущем пользователе  

## Архитектура проекта

> Проект разделён на логические слои. Каждый слой находится в отдельном пакете и отвечает только за свою зону ответственности.

### 1. Web слой (`web`)  
Отвечает за взаимодействие с клиентом.  

- `controller` - принимает HTTP-запросы, вызывает сервисы, возвращает ответы:  
  - AuthController.java    - Контроллер для регистрации/авторизации
  - GameController.java    - Контроллер для игры 
- `model` - модели запросов и ответов (DTO):  
  - AuthResponse.java      - DTO для ответов авторизации
  - GameRequest.java       - DTO для запроса игры 
  - GameResponse.java      - DTO для ответа игры 
  - SignUpRequest.java     - DTO для запроса регистрации
- `mapper` - преобразование web-моделей в доменные и обратно:  
  - WebGameMapper.java
- `filter` - валидация входящих запросов:
  - AuthFilter.java

**Web слой не содержит бизнес-логики.**

---

### 2. Domain слой (`domain`)
Содержит бизнес-логику приложения.

- `model` - доменные модели (игра, игровое поле, статус игры)
  -  User.java              - Модель пользователя в бизнес-логике
  -  Game.java              - Модель игры (уже есть)
  -  GameStatus.java        - Статус игры (уже есть)
- `service` - интерфейсы и реализации бизнес-логики (валидация, ход компьютера, проверка окончания игры)
  - UserService.java       - Интерфейс для работы с пользователями
  - GameService.java       - Интерфейс для игры (уже есть)
  - AuthService.java       - Логика аутентификации и авторизации

**Domain слой содержит бизнес-логику и сервисы. В текущей реализации использует репозитории web слоя напрямую.**  
   
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
Отвечает за конфигурацию зависимостей.

[//]: # (- определяет, какие реализации используются)

[//]: # (- связывает repository, service и другие компоненты)

[//]: # (- управляется Spring-контейнером)
- используется Spring Security FilterChain
- AuthFilter интегрирован вручную

---


## Структура проекта
```
src/
└── main/
    ├── java/org/tictactoe/
    │   ├── web/           # Web слой (контроллеры, DTO, фильтры)
    │   │   ├── controller/
    │   │   ├── filter/
    │   │   ├── model/
    │   │   └── webmapper/
    │   ├── domain/        # Бизнес-логика
    │   │   ├── model/
    │   │   └── service/
    │   ├── datasource/    # Работа с данными
    │   │   ├── model/
    │   │   ├── repository/
    │   │   └── mapper/
    │   └── di/           # Конфигурация зависимостей
    └── resources/
        ├── application.properties
        └── docker-compose.yml
```
## Поток данных в приложении
```
Клиент  
|  
V  
Веб (контроллер)  
|  
V  
Domain (сервис)   
|  
V  
Datasource (репозиторий)  
|  
V  
Storage  
```
