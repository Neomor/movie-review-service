FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/movie-review-system-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]