# ---- Build Stage ----
FROM gradle:8.5.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# ---- Run Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"] 