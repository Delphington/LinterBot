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

Для работы требуется БД `PostgreSQL`, `Redis`, `Kafka`.

Для дополнительной справки: [HELP.md](./HELP.md)



![{E4ED68AF-CD94-4964-B402-74AE70A10960}](https://github.com/user-attachments/assets/26e0773b-61db-41fb-b696-01e68d824b3a)


![Scrapper](https://github.com/user-attachments/assets/0a9cfa67-9f31-456f-a24d-24fbec93654e)<svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 7200.790836764663 2795.6894500653675" width="7200.790836764663" height="2795.6894500653675" class="excalidraw-svg">
  <!-- svg-source:excalidraw -->

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

Scrapper API работает по OpenAPI-контракту. В случае ошибок ошибки логируются, корректную обработку ошибок и пересылку сообщений в чат выполняет [`ErrorHandler`]().

### 📩 Получение обновлений

- Бот получает обновления о ссылках через [`UpdateController`]() по HTTP либо через [`KafkaUpdateListener`]() по Kafka.
- Scrapper отправляет данные по OpenAPI-контракту.
- Обновления рассылаются чатам через [`UpdateService`]().

### 📜 Дополнительно

- Бот поддерживает встроенное [**меню команд**]() в Telegram.

### 🧠 Кеширование

Бот кеширует ответы для следующих команд:
- /tag
- /tag <tag>
- /list

🔄 Кеш автоматически сбрасывается в следующих случаях:
- При добавлении или удалении ссылки (/track, /untrack)
- При удалении чата (/stop)

При вызове команд бот сначала проверяет наличие ответа в кеше. Если данные найдены — используется кеш. В противном случае происходит обращение к Scrapper API, и результат сохраняется в кеш.

---

## 🗂️ Scrapper

### 📌 Функционал

Scrapper обрабатывает запросы от бота:
- **Работа с чатами** через [`ChatController`]().
- **Работа с ссылками** через [`LinkController`]().
- **Работа с тегами** через [`TagController`]().

Все контроллеры работают по OpenAPI-контракту.

### 🔄 Получение обновлений

- [**`UpdateScheduler`**]().
- **📡 Источники данных:**
    - GitHub — через [`GitHubClient`]()
    - Stack Overflow — через [`StackOverflowClient`]()
- **⚙️ Обработка полученных данных**
    1. Запрос обновлений
        - Для каждого URL запрашивается обновление через API
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

Для хранения данных используются **четыре основные таблицы** и **три вспомогательные таблицы** для связи.

### 📌 Основные таблицы

- `chats` — таблица чатов.
- `links` — таблица ссылок.
- `tags` — таблица тегов.
- `filters` — таблица фильтров.

### 🔗 Связующие таблицы

Связи между чатами, ссылками, тегами и фильтрами реализованы через **промежуточные таблицы**:

- `chat_links` — связь между чатами и ссылками.
- `chat_link_tags` — связь между ссылками и тегами в контексте чата.
- `chat_link_filters` — связь между ссылками и фильтрами в контексте чата.

💡 **Один чат может отслеживать несколько ссылок, а одна ссылка может быть отслеживаемой несколькими чатами.**  
📌 **Каждая ссылка может иметь несколько тегов и фильтров в рамках одного чата.**

### 🛠 Способы работы с базой данных:

- **SQL** (`JdbcTemplate`, `SqlRepository`).
- **ORM** (`Hibernate`, `OrmRepository`).  
  Оба репозитория (`SqlRepository` и `OrmRepository`) наследуются от `DbRepository` и работают одинаково.  
  Выбор зависит от **настроек** (`database.access-type`).

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## 🚀 Запуск проекта

### 🔄 Клонирование репозитория

```bash

```

### 🏗️ Сборка

```bash

```

### ▶️ Запуск

#### 1️⃣ Запуск базы

```bash

```

#### 2️⃣ Запуск миграции

```bash

```

#### 3️⃣ Запуск redis

```bash

```

#### 4️⃣ Запуск zookeeper

```bash

```

#### 5️⃣ Запуск kafka

```bash

```

#### 6️⃣ Запуск скраппер

```bash

```

#### 7️⃣ Запуск  бота

```bash

```

