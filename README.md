![Build](https://github.com/central-university-dev/backend-academy-2025-spring-template/actions/workflows/build.yaml/badge.svg)

# Link Tracker

----

Чтобы бот заработал нужно в переменные среды загрузить TELEGRAM_TOKEN

----


[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-logo-6DB33F?logo=spring)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-logo-000?logo=apachekafka)](https://kafka.apache.org/)
[![Hibernate](https://img.shields.io/badge/Hibernate-logo-59666C?logo=hibernate)](https://hibernate.org/)
[![Docker](https://img.shields.io/badge/Docker-logo-2496ED?logo=docker)](https://www.docker.com/)
[![Testcontainers](https://img.shields.io/badge/Testcontainers-logo-000?logo=testcontainers)](https://testcontainers.com/)
[![Swagger](https://img.shields.io/badge/Swagger-logo-85EA2D?logo=swagger)](https://swagger.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-logo-336791?logo=postgresql)](https://www.postgresql.org/)


LinterBot - проект отслеживания обновлений по ссылкам. 
Состоит из двух независимых REST-сервисов: 
telegram-bot - общается с пользователем через telegram API, принимает вводимые пользователем URL-ы и отправляет на другой сервис - Scrapper
Scrapper обращается по открытому API к соответствующим сервисам для анализа возможных обновлений и уведомляет об изменениях сервис telegram-bot
	Технологии:
1) Сервисы используют два типа взаимодействия: через HTTP (REST) и асинхронное через Kafka
2) Postgres - для работы с базой данных: Hibernate + Spring JPA
3) Redis - для кэширование некоторых запросов
4) Liquibase - для управления  миграции БД
5) Docker - для развертывания Postgres, Liquibase и kafka
6) Spring Boot - для написания двух сервисов 
7) Swagger и OpenAPI для описания контрактов взаимодействия между сервисами
8) Testcontainers - тестирования

