[![CI](https://github.com/Romanow/data-migration-job/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Romanow/data-migration-job/actions/workflows/build.yml)
[![pre-commit](https://img.shields.io/badge/pre--commit-enabled-brightgreen?logo=pre-commit)](https://github.com/pre-commit/pre-commit)
[![Release](https://img.shields.io/github/v/release/Romanow/data-migration-job?logo=github&sort=semver)](https://github.com/Romanow/data-migration-job/releases/latest)
[![Docker Pulls](https://img.shields.io/docker/pulls/romanowalex/data-migration-job?logo=docker)](https://hub.docker.com/r/romanowalex/data-migration-job)
[![License](https://img.shields.io/github/license/Romanow/data-migration-job)](https://github.com/Romanow/data-migration-job/blob/main/LICENSE)

# Batch process for data migration

Скрипт представляет собой приложение командной строки и запускается как k8s Job. Подключается к двум БД (`SOURCE_*` и
`TARGET_*`) и поочередно перекладывает данные между таблицами, описанными в переменной `batch-processing.tables`. Пример
описания переменной `batch-processing.tables`:

```yaml
batch-processing:
  tables:
    - key-column-name: uid
      source:
        schema: public
        table: users
      target:
        schema: public
        table: users
```

Параметры подключения к SOURCE и TARGET базам данных ([`application.yml`](src/main/resources/application.yml)):

```yaml
spring:
  datasource:
    source:
      jdbc-url: jdbc:postgresql://${SOURCE_DATABASE_HOST:postgres}:${SOURCE_DATABASE_PORT:5432}/${SOURCE_DATABASE_NAME:source}
      username: ${SOURCE_DATABASE_USER:program}
      password: ${SOURCE_DATABASE_PASSWORD:test}
      driver-class-name: org.postgresql.Driver
    target:
      jdbc-url: jdbc:postgresql://${TARGET_DATABASE_HOST:postgres}:${TARGET_DATABASE_PORT:5432}/${TARGET_DATABASE_NAME:target}
      username: ${TARGET_DATABASE_USER:program}
      password: ${TARGET_DATABASE_PASSWORD:test}
      driver-class-name: org.postgresql.Driver
```

Для подключения внешней конфигурации нужно положить файл `/opt/config/external.yml`.

Пример запуска в файле [`docker-compose.yml`](docker-compose.yml).
