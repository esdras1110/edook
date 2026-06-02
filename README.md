# Edook

## Sobre o Projeto
Sistema automatizado onde todos os professores podem registrar previamente os instrumentos que utilizarão, contando depois com recursos de fácil acesso ao campo de reservas para saber quais e quantos itens estão disponíveis e controle para impedirem reservas simultâneas ao mesmo item no mesmo horário.

---

## Como Rodar o Banco de Dados (Docker) pelo CMD

### Pré-requisitos
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e aberto durante o processo.

### Inicialização
1. Abra o terminal na pasta do banco de dados:
   ```bash
   cd Edook\backend\banco_de_dados
2. Execute esse comando para iniciar o container do banco de dados
   ```bash
   docker compose up -d
3. Para executar comandos SQL pelo terminal execute esse comando:
   ```bash
   docker exec -it postgres_sistema psql -U admin_sistema -d Edook

### Finalizar Docker
1. Para sair do terminal do PostgreSQL execute:
   ```bash
   \q
2. Para parar o banco e remover os containers execute:
   ```bash
   docker compose down -v -> para e remove os containers

---

## Tecnologias Utilizadas

O projeto utiliza a linguagem **Java** como base principal para todo o ecossistema, dividindo-se em:

### Backend
* **Spring Boot**: Framework para criação da API REST e gerenciamento do sistema.
* **PostgreSQL**: Banco de dados relacional para armazenamento das informações.

### Frontend
* **JavaFX**: Framework para construção da interface gráfica para desktop.