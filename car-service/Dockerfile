# Stage 1: Build
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Copy the PARENT pom first
COPY . .

RUN mvn clean package -pl car-service -am -DskipTests
# Stage 2: Runtime
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

COPY --from=build /app/car-service/target/car-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]