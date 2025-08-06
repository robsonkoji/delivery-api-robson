@echo off
echo Executando testes com cobertura...
mvn clean verify -Ptest
echo Testes finalizados. Relatório: target\site\jacoco\index.html
pause