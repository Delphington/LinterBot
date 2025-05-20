FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
COPY target/bot-1.0.jar /app/bot-1.0.jar
EXPOSE 8080
CMD ["java", "-jar", "bot-1.0.jar"]
