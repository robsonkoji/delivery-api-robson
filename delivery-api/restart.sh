#!/bin/bash

echo "Reiniciando containers..."
docker-compose up -d

sleep 5

echo "Verificando chaves Redis pós reinício:"
docker exec -it redis redis-cli keys '*'

echo "Teste rápido API healthcheck:"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/actuator/health