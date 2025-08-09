# Stage 1: Build da aplicação com Maven + JDK 21 (imagem oficial, Alpine para build mais rápido e menor)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Diretório de trabalho dentro do container
WORKDIR /app

# Copia apenas os arquivos necessários para cachear dependências e acelerar build incremental
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Baixa as dependências sem compilar ainda (cache)
RUN mvn dependency:go-offline

# Copia o restante do código fonte
COPY src ./src

# Compila e empacota a aplicação sem rodar testes
RUN mvn clean package -DskipTests

# Stage 2: Imagem final leve para execução (JDK 21 Alpine)
FROM eclipse-temurin:21-jdk-alpine

# Diretório de trabalho da aplicação
WORKDIR /app

# Copia o jar gerado na etapa anterior para o container final
COPY --from=build /app/target/delivery-api-0.0.1.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Executa a aplicação usando o jar copiado
ENTRYPOINT ["java", "-jar", "app.jar"]
