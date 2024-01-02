# SpringBoot-Reactive-Security-JWT

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

### curl

```curl

curl  -X POST \
  'http://localhost:8080/v2/login' \
  --header 'Accept: */*' \
  --header 'Authorization: Basic dGlybWl6ZWU6dGlybWl6ZWU='
  
curl  -X POST \
  'http://localhost:8080/v1/login' \
  --header 'Accept: */*' \
  --header 'Content-Type: application/json' \
  --data-raw '{
  "username": "tirmizee",
  "password": "tirmizee"
}'

```

    
