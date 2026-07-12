# Edook

## Sobre o Projeto

O Edook é um sistema para gerenciamento de reservas de equipamentos escolares. Ele permite que professores realizem reservas antecipadas de equipamentos, evitando conflitos de horário e facilitando o controle dos recursos disponíveis.

---

# Pré-requisitos

Antes de executar o projeto é necessário possuir:

* Docker Desktop instalado;
* Docker Desktop em execução.

---

# Configuração do envio de e-mails

O sistema utiliza uma conta Gmail para enviar:

* Código de confirmação de e-mail;
* Código para redefinição de senha.

O sistema já vem com uma conta genérica para realizar essa função que pode ser alterada em:

```text
backend/src/main/resources/application.properties
```
Campos a serem alterados:
```properties
spring.mail.username=SEU_EMAIL@gmail.com
spring.mail.password=SUA_SENHA_DE_APLICATIVO
```

**Importante:** a senha utilizada deve ser uma **Senha de Aplicativo** gerada pela conta Google.

---

# Executando o projeto

Na pasta `backend`, execute:

```bash
docker compose up --build
```

Na primeira execução o Docker irá:

* baixar as imagens necessárias;
* criar o banco de dados PostgreSQL;
* compilar o projeto Spring Boot;
* iniciar a API.

Esse processo pode levar alguns minutos.

---

# Acessando a documentação da API

Após iniciar a aplicação, a documentação Swagger estará disponível em:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Acessando o banco de dados

Caso seja necessário executar comandos SQL diretamente no PostgreSQL:

```bash
docker exec -it postgres_sistema psql -U admin_sistema -d Edook
```

Para sair do terminal do PostgreSQL:

```bash
\q
```

---

# Encerrando a aplicação

Parar os containers mantendo os dados do banco:

```bash
docker compose down
```

Parar os containers e remover os dados do banco:

```bash
docker compose down -v
```

---

# Tecnologias utilizadas

## Backend

* Java 21
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Docker
* Swagger (Springdoc OpenAPI)

## Frontend

* Java 25
* JavaFX
