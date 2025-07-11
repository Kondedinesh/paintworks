# ===== Stage 1: Build =====
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies first (cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ===== Stage 2: Run =====
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the jar file from the previous build stage
COPY --from=build /app/target/paintworks-1.0.0.jar app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
