# edook - Frontend

O **edook** é uma aplicação de gerenciamento e reserva de equipamentos desenvolvida em JavaFX. O sistema funciona como o cliente (frontend) que consome uma API REST (backend) para automatizar o fluxo de agendamentos de funcionários em uma instituição.

---

## Funcionalidades

* **Login:** Autenticação segura com validação de credenciais junto ao servidor.
* **Painel de Início:** Dashboard com visão geral dos lembretes e reservas recentes.
* **Gestão de Reservas:** Tabela dinâmica com múltiplos filtros acumulativos (busca textual, filtros avançados de data/local e alternância de estado para exibir "Apenas Minhas Reservas").
* **Perfil do Usuário:** Visualização de dados e modo de edição em tela única (evitando navegações desnecessárias), além de redefinição de senha com pop-up modal.
* **Cancelamento em Lote:** Capacidade de selecionar múltiplos itens na tabela para cancelamentos simultâneos.
* **Gestão de Usuários:** Cadastro e exclusão de contas de usuários.
* **Gestão de Equipamentos:** Cadastro, edição e exclusão de equipamentos.

---

## Arquitetura e Técnicas Utilizadas

Este projeto foi construído seguindo padrões de arquitetura de software:

* **Padrão Singleton:** Implementado na classe `UserSession` para gerenciar o estado global do usuário autenticado de forma segura e centralizada.
* **Programação Assíncrona:** Uso do `HttpClient` nativo do Java com `sendAsync()` e `Platform.runLater()`, garantindo que a interface gráfica nunca trave durante as requisições à API.
* **Callbacks:** Uso da interface `Runnable` para comunicação entre telas (ex: telas de sucesso), reduzindo o acoplamento entre os controladores JavaFX.
* **Sistema de Módulos (JPMS):** Configuração nativa com `module-info.java` e controle estrito de reflexão (`opens`) para o JavaFX e a biblioteca Jackson.
* **UI/UX Avançada:** Efeito visual de desfoque (`GaussianBlur`) dinâmico em janelas de fundo ao abrir pop-ups modais.

---

## Tecnologias e Dependências

### Tecnologias Core
* **Java 25** (Linguagem e ambiente de execução)
* **JavaFX 21.0.6** (Framework para construção da interface gráfica)
* **Maven** (Gerenciador de dependências e automação de build)

### Dependências (Bibliotecas Externas)
* **Jackson Databind & JSR310 (v2.17.2):** Processamento, desserialização de JSON e suporte a datas do Java 8+.
* **JUnit 5.12.1:** Framework para a estrutura e execução dos testes unitários.

---

## Estrutura do Projeto

Abaixo está a disposição dos principais arquivos e pacotes do ecossistema do projeto:

```text
edook-frontend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/edook/frontend/
│   │   │   │   ├── components/             # Componentes visuais customizados e reutilizáveis
│   │   │   │   ├── controllers/            # Controladores JavaFX (ligação lógica-FXML)
│   │   │   │   ├── models/                 # DTOs para transporte e mapeamento dos dados JSON
│   │   │   │   ├── session/                # Gerenciamento da sessão global (UserSession)
│   │   │   │   └── HelloApplication.java   # Classe principal de inicialização
│   │   │   └── module-info.java            # Descritor do Módulo (Configuração do JPMS)
│   │   └── resources/com/edook/frontend/   # Telas e modais construídos em FXML
│   │       ├── fonts/                      # Fontes textuais customizadas
│   │       ├── images/                     # Ícones e imagens estáticas
├── pom.xml                                 # Configurações do Maven e dependências
└── INSTALL.MD                              # Guia de instalação e execução
```

## Como Rodar a Aplicação

As instruções completas de pré-requisitos, execução em modo de desenvolvimento e o instalador rápido (wizard) foram movidas para um documento separado para melhor organização.

**[Guia de Instalação e Execução (INSTALL.MD)](./INSTALL.MD)**