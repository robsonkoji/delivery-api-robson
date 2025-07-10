# 🚚 Delivery Tech API

Sistema de delivery desenvolvido com Java 21 e Spring Boot, utilizando recursos modernos da linguagem e uma arquitetura simples para testes e desenvolvimento local.

## 🛠️ Tecnologias Utilizadas

Java 21 (LTS)

Spring Boot 3.5.5

Spring Web

Spring Data JPA

Banco de Dados: H2 (em memória)

Build Tool: Maven

## ⚙️ Recursos Modernos da Linguagem

Este projeto utiliza funcionalidades recentes do Java:

✅ Records (Java 14+)

✅ Text Blocks (Java 15+)

✅ Pattern Matching (Java 17+)

✅ Virtual Threads (Java 21)


## ▶️ Como Executar o Projeto

Certifique-se de ter o JDK 21 instalado.

```bash
# Clone o repositório
git clone https://github.com/robsonkoji/delivery-api-robson.git
cd delivery-api
````
```
# Execute a aplicação
./mvnw spring-boot:run
Acesse a aplicação em: http://localhost:8080/health
````
---

## 📡 Endpoints Disponíveis
| Método | Endpoint |	Descrição |
|--------|--------|----------|
|GET	   |/health	|Verifica o status da aplicação e versão do Java|
|GET     |/info	  |Retorna informações da aplicação|
|GET	   |/h2-console	|Console web do banco H2 (em memória)|

## 🔧 Configuração Padrão
* Porta: 8080
* Banco de Dados: H2 (em memória)
* Perfil Ativo: development

## 👤 Desenvolvedor:
Robson Koji

Turno: Tarde

Desenvolvido com JDK 21 e Spring Boot 3.5.5