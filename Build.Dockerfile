# Этап 1: Сборка Gradle проекта
FROM gradle:7.6-jdk17 AS build
WORKDIR /app

# Копируем build.gradle и загружаем зависимости
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

# Копируем исходный код и собираем приложение
COPY src ./src
RUN gradle build -x test --no-daemon

# Этап 2: Подготовка runtime-окружения
FROM eclipse-temurin:21-jre
WORKDIR /app

# Копируем jar файл из этапа сборки
COPY --from=build /app/build/libs/*.jar app.jar

# Копируем папку с статическими файлами в контейнер
COPY --from=build /app/src/main/resources/static /app/static

# Указываем команду для запуска Spring Boot приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
