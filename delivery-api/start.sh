#!/bin/bash

echo "Construindo e iniciando containers..."
docker-compose up --build -d

echo "Aguardando 10 segundos para inicialização..."
sleep 10

echo "Mostrando status dos containers:"
docker-compose ps

echo "Mostrando últimos 20 logs da API:"
docker-compose logs delivery-api --tail=20

echo "Teste rápido de healthcheck API (porta 8080):"
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/actuator/health

echo "Teste de Redis: chaves armazenadas"
docker exec -it redis redis-cli keys '*'

echo "Acessar:"
echo "- Jaeger: http://localhost:16686"
echo "- Prometheus: http://localhost:9090"
echo "- Grafana: http://localhost:3000 (usuário/senha: admin/admin)"
echo "- RedisInsight: http://localhost:5540"