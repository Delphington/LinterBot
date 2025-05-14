FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
COPY target/scrapper.jar /app/scrapper.jar
EXPOSE 8081
CMD ["java", "-jar", "scrapper.jar"]
