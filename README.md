# SpringBoot-Reactive-Security-JWT

- Create and manage a access token.
- Create and manage a refresh token.

## Flow

  ![refresh-token](https://github.com/tirmizee/SpringBoot-Reactive-Security-JWT/assets/15135199/c55cb010-7f10-439d-9336-39c9143fdd97)

### docker-compose for redis and database

```yaml

version: '3.8'

services:
  redis:
    image: bitnami/redis:6.2.6
    platform: linux/amd64
    container_name: redis-single
    ports:
      - '6379:6379'
    environment:
      - REDIS_PASSWORD=password123
    networks:
      - backend-network
  db:
    image: postgres:14.1-alpine
    environment:
      POSTGRES_USER: usr
      POSTGRES_PASSWORD: pass
      POSTGRES_DB: test_db
    ports:
      - '5432:5432'
    volumes:
      - ./init-schema.sql:/docker-entrypoint-initdb.d/01_init_schema.sql
      - ./init-data.sql:/docker-entrypoint-initdb.d/02_init_data.sql
    networks:
      - backend-network
networks:
  backend-network:

```

### application.yaml

```yaml
spring:
  redis:
    host: 0.0.0.0
    port: 6379
    password: password123
  r2dbc:
    url: r2dbc:postgresql://0.0.0.0:5432/test_db?schema=schema_sit
    username: usr
    password: pass
    pool:
      enabled: true
      initialSize: 5
      maxSize: 10
      validationQuery: SELECT 1
่jwt:
  secret: 11111111111111111111111111111111
  access-expiration: 36000
  refresh-expiration: 360000
logging.level:
  org.springframework.r2dbc: DEBUG


```

### curl

```curl

curl  -X POST \
  'http://localhost:8080/v2/login' \
  --header 'Accept: */*' \
  --header 'X-Forwarded-For: 184.22.63.64' \
  --header 'Authorization: Basic dGlybWl6ZWU6dGlybWl6ZWU='
  
curl  -X POST \
  'http://localhost:8080/v1/login' \
  --header 'Accept: */*' \
  --header 'Content-Type: application/json' \
  --header 'X-Forwarded-For: 184.22.63.64' \
  --data-raw '{
  "username": "tirmizee",
  "password": "tirmizee"
}'

curl  -X GET \
  'http://localhost:8080/profile' \
  --header 'Accept: */*' \
  --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
  --header 'X-Forwarded-For: 184.22.63.64' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aXJtaXplZSIsImlwIjoiMTg0LjIyLjYzLjY0IiwiaWF0IjoxNzA0MjcxMzE0LCJleHAiOjE3MDQyNzEzNTB9.xSDQgAEnLdg3n35yO3UGG8UJkl15zYeaR2I9EtOnaRI'

curl  -X POST \
  'http://localhost:8080/v1/refresh/8dbab5d9-d941-4fb9-af55-59dfe49e308c' \
  --header 'Accept: */*' \
  --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
  --header 'X-Forwarded-For: 184.22.63.64'

```

    
