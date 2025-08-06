#!/bin/bash

echo "⏳ Executando testes com cobertura..."

mvn clean verify -Ptest

echo "✅ Testes finalizados. Relatório disponível em:"
echo "target/site/jacoco/index.html"