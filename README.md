# Journal Backend
Spring Boot REST API backend for a journal-writing social media. This application is built to learn the Spring framework
and showcase it.

# Docker
Can be run using docker:
```
Docker compose -f docker-compose.yaml up
```
[wait-for-it.sh](https://github.com/vishnubob/wait-for-it) is used to wait for MySQL to start before allowing spring to
boot itself up. If this fails or not used, then ```restart: on-failure``` is set on docker compose  

# Without Docker
Modify application.properties datasource to:
```
spring.datasource.url=jdbc:mysql://localhost:3306(or your custom port)/journal_db?serverTimezone=UTC
```

Create a database in MySQL:
```
CREATE DATABASE journal_db;
```


