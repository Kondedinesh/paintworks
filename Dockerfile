# Use an OpenJDK base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the jar from target to app directory
COPY target/paintworks-1.0.0.jar app.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
