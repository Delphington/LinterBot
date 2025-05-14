FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
COPY target/bot.jar /app/bot.jar
EXPOSE 8080
CMD ["java", "-jar", "bot.jar"]
