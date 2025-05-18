# 📌 Link Tracker

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-logo-6DB33F?logo=spring)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-logo-000?logo=apachekafka)](https://kafka.apache.org/)
[![Hibernate](https://img.shields.io/badge/Hibernate-logo-59666C?logo=hibernate)](https://hibernate.org/)
[![Docker](https://img.shields.io/badge/Docker-logo-2496ED?logo=docker)](https://www.docker.com/)
[![Testcontainers](https://img.shields.io/badge/Testcontainers-logo-000?logo=testcontainers)](https://testcontainers.com/)
[![Swagger](https://img.shields.io/badge/Swagger-logo-85EA2D?logo=swagger)](https://swagger.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-logo-336791?logo=postgresql)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-logo-DC382D?logo=redis)](https://redis.io/)

----

Чтобы бот заработал нужно в переменные среды загрузить TELEGRAM_TOKEN

----

## 📝 Описание проекта

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 23` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Для работы требуется `PostgreSQL`, `Redis`, `Kafka`.

### 📟 Схема приложения 
![Scrapper](https://github.com/user-attachments/assets/0a9cfa67-9f31-456f-a24d-24fbec93654e)<svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 7200.790836764663 2795.6894500653675" width="7200.790836764663" height="2795.6894500653675" class="excalidraw-svg">
---

## 🤖 Бот

### 📌 Функционал

Бот поддерживает следующие команды:

- [**`/start`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/command/helper/StartCommand.java) — регистрация пользователя.
- [**`/help`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/command/helper/HelpCommand.java) — список всех доступных команд.
- [**`/track`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/command/link/TrackCommand.java) — добавление ссылки для отслеживания 
- [**`/untrack`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/command/link/UntrackCommand.java) — удаление ссылки из списка отслеживаемых.
- [**`/list`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/command/link/ListCommand.java) — получение списка всех отслеживаемых ссылок.
- [**`/tag`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/command/tag/TagCommand.java) — выводит все ссылки, у которых есть определенный тег (/tag < tag >)
- [**`/taglist`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/command/tag/TagListCommand.java) — все теги, которые ввел пользователь
- [**`/untag`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/command/tag/UnTagCommand.java) — удаление тега у ссылки (/untag < tag > < link >)
- [**`/filter`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/command/filter/FilterCommand.java) — позволяет добавить фильтр
- [**`/filterlist`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/command/filter/FilterListCommand.java) — выводит все фильтры пользователя
- [**`/unfilter`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/command/filter/UnFilterCommand.java) — удаление фильтров (/unfilter < filter >)


### 🔄 Взаимодействие со Scrapper

Бот общается со Scrapper API через:
- [**`ScrapperTgChatClient`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/client/chat/ScrapperTgChatClientImpl.java) — регистрация и удаление чатов.
- [**`LinkClient`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/client/link/ScrapperLinkClientImpl.java) — управление ссылками (добавление, удаление, получение списка ссылок).
- [**`TagClient`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/client/tag/ScrapperTagClientImpl.java) — получение списка тегов, получение ссылки по тегу и удаление тега у ссылки.
- [**`ScrapperFilterClient`**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/client/filter/ScrapperFilterClientImpl.java) — управление фильтрами (добавление, удаление, получение списка фильтров)

### 📩 Получение обновлений

- Бот получает обновления о ссылках через [`UpdateController`](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/api/controller/UpdateController.java) по HTTP либо через [`KafkaUpdateListener`](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/kafka/client/KafkaLinkUpdateListener.java) по Kafka.
- Scrapper отправляет данные по OpenAPI-контракту.
- Обновления рассылаются чатам через [`NotificationService`](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/notification/NotificationService.java).

### 📜 Дополнительно

- Бот поддерживает встроенное [**меню команд**](https://github.com/Delphington/LinterBot/blob/main/bot/src/main/java/backend/academy/bot/processor/UserMessageProcessor.java) в Telegram.

### 🧠 Кеширование Redis

Бот кеширует ответы для следующих команд:
- /tag
- /tag <tag>
- /list

🔄 Кеш автоматически сбрасывается в следующих случаях:
- При добавлении или удалении ссылки (/track, /untrack, /untag)

При вызове команд бот сначала проверяет наличие ответа в кеше. Если данные найдены — используется кеш. В противном случае происходит обращение к Scrapper API, и результат сохраняется в кеш.

---

## 🗂️ Scrapper

### 📌 Функционал

Scrapper обрабатывает запросы от бота:
- **Работа с чатами** через [`ChatController`](https://github.com/Delphington/LinterBot/blob/main/scrapper/src/main/java/backend/academy/scrapper/controller/ChatController.java).
- **Работа с ссылками** через [`LinkController`](https://github.com/Delphington/LinterBot/blob/main/scrapper/src/main/java/backend/academy/scrapper/controller/LinkController.java).
- **Работа с тегами** через [`TagController`](https://github.com/Delphington/LinterBot/blob/main/scrapper/src/main/java/backend/academy/scrapper/controller/TagController.java).
- **Работа с фильтрами** через [`FilterController`](https://github.com/Delphington/LinterBot/blob/main/scrapper/src/main/java/backend/academy/scrapper/controller/FilterController.java).

Все контроллеры работают по OpenAPI-контракту.

### 🔄 Получение обновлений

- [**`LinkUpdateScheduler`**](https://github.com/Delphington/LinterBot/blob/main/scrapper/src/main/java/backend/academy/scrapper/scheduler/LinkUpdaterScheduler.java).
- **📡 Источники данных:**
    - GitHub — через [`GitHubClient`](https://github.com/Delphington/LinterBot/blob/main/scrapper/src/main/java/backend/academy/scrapper/tracker/client/GitHubClient.java)
    - Stack Overflow — через [`StackOverflowClient`](https://github.com/Delphington/LinterBot/blob/main/scrapper/src/main/java/backend/academy/scrapper/tracker/client/StackOverFlowClient.java)
- **⚙️ Обработка полученных данных**
    1. Запрос обновлений
        - Для каждого URL запрашивается обновление через открытый API GitHub и StackOverflow
    2. Фильтрация обновлений
        - Определяются подписанные пользователи (чаты), которые отслеживают данный URL.
        - Для каждого пользователя применяется его список фильтров:
        1. Ключевые фильтры — фильтрация по ключевым словам (issue, commit и т.д).
        2. Анти-фильтры по пользователям — если указано user=username, то обновления от этого пользователя игнорируются.
    3. Парсинг ответа
        - Полученный JSON-ответ анализируется, извлекаются значения, соответствующие фильтрам.
    4. Проверка актуальности
        - Обновление считается релевантным, если оно произошло после последнего запуска шедулера.
    5. Формирование уведомлений
        - Отобранные обновления, соответствующие фильтрам, отправляются пользователям либо через HTTP, либо через Kafka.
- **⚙️ Обработка батчей и многопоточность**
    - Ссылки на обновления запрашиваются партиями (batch) заданного размера. Каждый батч делится между потоками. Количество потоков настраивается через конфигурацию.

## 📦 Хранение данных

- `tg_chats` — таблица чатов.
- `links` — таблица ссылок.
- `tags` — таблица тегов.
- `filters` — таблица фильтров.
- `filter_list` — таблица фильтров каждого пользователя

### 📌 Схема базы данных

![{E4ED68AF-CD94-4964-B402-74AE70A10960}](https://github.com/user-attachments/assets/26e0773b-61db-41fb-b696-01e68d824b3a)

💡 **Один чат может отслеживать несколько ссылок, а одна ссылка может быть отслеживаемой несколькими чатами.**  
📌 **Каждая ссылка может иметь несколько тегов и фильтров в рамках одного чата.**

### 🛠 Способы работы с базой данных:

- **SQL** (`JdbcTemplate`, `Dao`).
- **ORM** (`Hibernate`, `OrmRepository`).  
  Выбор зависит от **настроек** (`database.access-type`).

----


## 🗂️ Метрики

#### RED
![изображение](https://github.com/user-attachments/assets/e242b8e5-34e5-406f-9ee3-761fd25a7d87)

#### Custom
  - Количество пользовательских сообщений в секунду
  - График количества активных ссылок в БД по типу (github, stackoverflow)
  - p50, p95, p99 времени работы одного scrape по типу (github, stackoverflow)

![{1B937D0F-0951-4F80-9F3A-DF26FE002291}](https://github.com/user-attachments/assets/bf593a10-19ca-41dd-a3e4-ef42d0c78a20)

#### jvm
![{665E0A37-2D76-45E2-B08D-494A4515D463}](https://github.com/user-attachments/assets/57d6604c-28c4-419a-9f70-77d19927c95f)


---


## 🚀 Запуск проекта

1. Клонирование репозитория специальной ветки
    ```bash
      git clone -b feat-docker --single-branch https://github.com/Delphington/LinterBot
    ```

2. Перейдите в директорию с docker-конфигурацией:
   ```bash
    cd LinterTelegramBot
   ```
   
3. Нужно прокинуть специальную переменную среды
    ```bash
      $env:TELEGRAM_TOKEN="YOUR_TOKEN"
    ```

4. Запустите сборку и запуск контейнеров:
    ```bash
      docker-compose up --build
    ```
