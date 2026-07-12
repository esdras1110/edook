# ==========================
# Etapa 1 - Compilar o projeto
# ==========================
FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

# Copia todo o projeto
COPY . .

# Compila o projeto e gera o .jar
RUN mvn clean package -DskipTests

# ==========================
# Etapa 2 - Executar a aplicação
# ==========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia apenas o .jar gerado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]