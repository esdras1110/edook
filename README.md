# Edook

## Sobre o Projeto

O Edook é um sistema para gerenciamento de reservas de equipamentos escolares. Ele permite que professores realizem reservas antecipadas de equipamentos, evitando conflitos de horário e facilitando o controle dos recursos disponíveis.

---

## Arquitetura do Projeto

O ecossistema é dividido em duas partes independentes e complementares:

*  **Backend (API REST):** Desenvolvido em **Java 21** com **Spring Boot**, utilizando **PostgreSQL** como banco de dados e empacotado via **Docker**.
*  **Frontend (Cliente Desktop):** Desenvolvido em **Java 25** utilizando o framework gráfico **JavaFX 21.0.6** para uma experiência nativa de desktop.
*  Para demais integrações especificas, basta acessar o README de cada parte especificamente, como demonstra o diretório abaixo.


```text
edook/
├──  backend       --> API REST, Banco de Dados e Documentação Swagger
└──  frontend      --> Interface gráfica Desktop (JavaFX) e Telas FXML
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
