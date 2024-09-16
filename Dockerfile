# Wybierz obraz bazowy Java
FROM openjdk:21-jdk-slim

# Ustaw zmienną środowiskową dla nazwy aplikacji (opcjonalnie)
ENV APP_NAME=companyservice

EXPOSE 8080

# Skopiuj plik JAR aplikacji do obrazu
COPY target/*.jar app.jar

# Ustaw punkt wejścia dla aplikacji
ENTRYPOINT ["java", "-jar", "app.jar"]
