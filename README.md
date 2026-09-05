# Cloud File Storage

Приложение позволяет пользователям хранить свои файлы и папки в облачном хранилище.

Каждый пользователь имеет собственное файловое пространство и не имеет доступа к файлам других пользователей.

## Backend

Для разработки backend использовались:

* **Java 21**
* **Spring Boot**
* **Spring Security**
* **Spring Data JPA / Hibernate**
* **PostgreSQL** — хранение пользователей
* **Flyway** — миграции базы данных
* **Redis** — хранение пользовательских сессий
* **MinIO (S3)** — хранение файлов
* **Swagger / OpenAPI** — документация REST API
* **JUnit + Testcontainers** — интеграционное тестирование
* **Maven** — сборка проекта

## Функциональные возможности

### Пользователи

* регистрация;
* авторизация;
* выход из аккаунта;
* сохранение сессии пользователя.

### Файлы и папки

* загрузка файлов;
* загрузка папок с вложенными файлами;
* создание папок;
* просмотр содержимого папок;
* удаление файлов и папок;
* переименование файлов и папок;
* перемещение файлов и папок;
* скачивание файлов;
* скачивание папок в формате ZIP;
* поиск файлов и папок.

## Docker

Docker Compose используется для запуска:

* PostgreSQL;
* Redis;
* MinIO;
* Frontend.

Backend запускается отдельно как Spring Boot JAR.

## Инструкции по запуску

### Локальный запуск
1-Создаем рядом с docker-compose.yml файл .env и прописываем там учетные данные для всех требуемых технологий,либо
напрямую меняем в docker-compose.yml
POSTGRES_USER=you_postgres_user
POSTGRES_PASSWORD=you_postgres_password
MINIO_ROOT_USER=you_minio_user
MINIO_ROOT_PASSWORD=you_minio_password
2-Эти же данные прописываем в application.properties либо напрямую,либо через переменные окружения
Переменные окружения проекта в Idea находятся в Edit->Environment variables и прописываются таким образом
POSTGRES_USER=you_postgres_user;
POSTGRES_PASSWORD=you_postgres_password;
MINIO_ROOT_USER=you_minio_user;
MINIO_ROOT_PASSWORD=you_minio_password
3-Устанавливаем docker и через cmd в папке с docker-compose.yml пишем
docker compose up --build -d что создаст и запустит все контейнеры
4-Запускаем бэкенд и можем заходить на http://localhost:3000 для проверки работоспособности

### Запуск на удаленном сервере
1-Создаем JAR бэкенда(если не хотим потом подставлять пользователские значения то сразу меняем в application.properties)
2-Выполняем пункт 1 локального запуска
3-Копируем JAR,docker-compose.yml,.env докера, фронтенд на удаленный сервер в одну папку
4-Устанавливаем на удаленном сервере docker, docker compose,JRE
5-Выполняем пункт 3 локального запуска
6-Выполняем запуск JAR с переменными окружения 
POSTGRES_USER=you_postgres_user POSTGRES_PASSWORD=you_postgres_password MINIO_ROOT_USER=you_minio_user MINIO_ROOT_PASSWORD=you_minio_password java -jar cloud-file-storage-0.0.1-SNAPSHOT.jar

