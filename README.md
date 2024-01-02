# SpringBoot-Reactive-Security-JWT

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

    
