#!/bin/bash

echo "Parando containers..."
docker-compose down

echo "Volumes ainda existem, para limpar manualmente use:"
echo "docker volume ls"
echo "docker volume rm <nome_volume>"
