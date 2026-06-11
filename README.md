# Edook

## Sobre o Projeto
Sistema automatizado onde todos os professores podem registrar previamente os instrumentos que utilizarão, contando depois com recursos de fácil acesso ao campo de reservas para saber quais e quantos itens estão disponíveis e controle para impedirem reservas simultâneas ao mesmo item no mesmo horário.

---

## Como rodar o Docker e testar pelo Swagger

### Compilar a API
Para que o Docker consiga executar, ele precisa acessar o JAR contendo todas as informações que ele precisa para rodar.

1. Para gerar esse JAR abra o terminal na pasta do backend e execute:
   ```bash
   mvnw.cmd clean package -DskipTests
   ```
### Executar o Docker
Com o JAR da API gerado é possivel executar o docker
1. Na pasta do backend, execute:
   ```bash
   docker compose up -d --build
   ```
2. É possivel executar comandos SQL pelo proprio terminal. Para isso, execute esse comando na mesma pasta:
   ```bash
   docker exec -it postgres_sistema psql -U admin_sistema -d Edook
   ```

### Como acessar o Swagger
Com o docker rodando, acesse pelo seu navegador: http://localhost:8080/swagger-ui/index.html#.


### Finalizar Docker
1. Para sair do terminal do PostgreSQL execute:
   ```bash
   \q
   ```
2. Para apenas parar o docker e manter os dados do banco execute:
   ```bash
   docker compose down
   ```
3. Para parar e remover os dados execute:
   ```bash
   docker compose down -v
   ```
### Pré-requisitos
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e aberto durante o processo.

---

## Tecnologias Utilizadas

O projeto utiliza a linguagem **Java** como base principal para todo o ecossistema, dividindo-se em:

### Backend
* **Spring Boot**: Framework para criação da API REST e gerenciamento do sistema.
* **PostgreSQL**: Banco de dados relacional para armazenamento das informações.

### Frontend
* **JavaFX**: Framework para construção da interface gráfica para desktop.
