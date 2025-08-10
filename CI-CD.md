# Pipeline CI/CD - Delivery API

## Visão Geral

Este pipeline automatiza as etapas de **build**, **testes**, **deploy** e **validação** da aplicação Delivery API usando GitHub Actions. Ele garante que o código seja testado, empacotado em imagem Docker, enviado para o Docker Hub e implantado automaticamente nos ambientes de homologação e produção.

---

## Gatilhos

O pipeline é disparado automaticamente a cada **push** ou **pull request** nas branches:

- `develop` — ambiente de homologação
- `main` — ambiente de produção

---

## Etapas do Pipeline

### 1. Build e Testes

- Realiza checkout do código.
- Compila a aplicação com Maven.
- Executa testes unitários e de integração (`mvn clean verify`).
- Objetivo: garantir que o código está funcionando antes de prosseguir.

### 2. Build e Push da Imagem Docker

- Login no Docker Hub usando secrets.
- Cria a imagem Docker da aplicação.
- Envia a imagem para o Docker Hub com tag `latest`.

### 3. Deploy Automático em Homologação (`develop`)

- Deploy via SSH no servidor de homologação.
- Atualiza os containers Docker com a nova imagem.
- Valida se a aplicação está rodando acessando o endpoint `/health`.

### 4. Deploy em Produção (`main`)

- Deploy via SSH no servidor de produção.
- Atualiza os containers Docker com a nova imagem.

---

## Como Monitorar o Pipeline

- Na aba **Actions** do GitHub, acompanhe o status das execuções, logs e eventuais falhas.
- Configure notificações para receber alertas de sucesso ou erro (ex: email, Slack).
- Acesse logs diretamente no servidor para análise pós-deploy.

---

## Secrets Necessários

- `DOCKER_USERNAME` e `DOCKER_PASSWORD`: credenciais Docker Hub.
- `SERVER_HOST`, `SERVER_USER`, `SERVER_SSH_KEY`: acesso SSH para produção.
- `HOMOLOG_SERVER_HOST`, `HOMOLOG_SERVER_USER`, `HOMOLOG_SERVER_SSH_KEY`: acesso SSH para homologação.

**Atenção:** Nunca compartilhe ou exponha essas informações em código público.

---

## Requisitos da Aplicação

- A aplicação deve ter um endpoint `/health` para que a validação automática após deploy funcione corretamente.

---

## Possíveis Extensões Futuras

- Análise estática de código.
- Testes de carga.
- Deploy canário.
- Notificações em múltiplos canais.

---

## Contato

Para dúvidas ou melhorias no pipeline, abra uma issue neste repositório ou entre em contato com o time de DevOps.

---

