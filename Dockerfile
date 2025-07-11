FROM openjdk:21-jdk

WORKDIR /app

COPY target/paintworks-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
