plugins {
    java
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20" // gradle
}

group = "org.tictactoe"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_18
}

repositories {
    mavenCentral()
}

dependencies {

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("org.springframework.boot:spring-boot-starter-web")
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

//     Spring Security для авторизации
    implementation("org.springframework.boot:spring-boot-starter-security")

    // База данных
    runtimeOnly("org.postgresql:postgresql")  // runtimeOnly вместо implementation
    //    implementation("org.postgresql:postgresql")

    implementation("org.springframework.boot:spring-boot-starter") // Явное подключение Spring Core

//    // Тестирование
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testImplementation("org.springframework.security:spring-security-test") // Для тестов безопасности
}
