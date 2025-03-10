![Build](https://github.com/central-university-dev/backend-academy-2025-spring-template/actions/workflows/build.yaml/badge.svg)


[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-logo-6DB33F?logo=spring)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-logo-316192?logo=postgresql)](https://www.postgresql.org/)
[![Hibernate](https://img.shields.io/badge/Hibernate-logo-59666C?logo=hibernate)](https://hibernate.org/)
[![Docker](https://img.shields.io/badge/Docker-logo-2496ED?logo=docker)](https://www.docker.com/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-logo-000?logo=apachekafka)](https://kafka.apache.org/)
[![Liquibase](https://img.shields.io/badge/Liquibase-logo-418B8E?logo=liquibase)](https://www.liquibase.org/)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-logo-6DB33F?logo=spring)](https://spring.io/projects/spring-data-jpa)
[![Swagger](https://img.shields.io/badge/Swagger-logo-85EA2D?logo=swagger)](https://swagger.io/)



# Link Tracker
LinterBot - проект отслеживания обновлений по ссылкам. 
Состоит из двух REST-сервисов: 
- telegram-bot - общается с пользователем через telegram API и принимает вводимые пользователем URL-ы и отправляет на другой сервис - Scrapper
- Scrapper обращается по открытому API к соответствующим сервисам для анализа возможных обновлений и уведомляет об изменениях сервис telegram-bot,
	Технологии:
    - Telegram-бот и Scrapper используют два типа взаимодействия: через HTTP (REST) и асинхронное через Kafka
    - Postgres - бд для хранения данных через Hibernate + Spring JPA
    - Liquibase - для миграции
    - Docker - для развертывания Postgres, Liquibase и kafka
    - Spring Boot - для написания двух сервисов 
    - Swagger и OpenAPI для описания контрактов взаимодействия сервисов


![{56F555B7-486F-4E68-958A-0A45F16291E0}](https://github.com/user-attachments/assets/c8550ffb-5659-4d16-b886-12aa175f64a1)


