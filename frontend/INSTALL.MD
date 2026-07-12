# Instruções de Instalação e Execução
Este documento orienta o processo de configuração do ambiente, execução em modo de desenvolvimento e geração do instalador nativo para o frontend do projeto edook.

---

## Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:
1. **JDK 25** (Certifique-se de que a variável de ambiente `JAVA_HOME` está apontando para esta versão).
2. **Apache Maven** (Configurado e acessível via terminal).

---

## Instalação Rápida

Se você deseja apenas testar a aplicação sem precisar configurar ferramentas de desenvolvimento (como Java ou Maven), basta utilizar o nosso instalador oficial:
1. Acesse a página de **[Releases do Repositório](https://github.com/esdras1110/edook/releases)**.
2. Na versão mais recente, baixe o arquivo `Edook-1.0.0.exe`.
3. Execute o arquivo baixado e siga o assistente de instalação (Wizard).
4. O programa foi instalado, caso não encontre atalho na Área de Trabalho ou no Menu Iniciar, basta pesquisar por Edook.

Obs: O instalador compacta apenas os dados do frontend, programa do cliente/usuário, certifique-se de que a API do backend juntamente do banco estão rodando antes de testar, pois esses módulos ainda não foram hospedados em um servidor.

---

## Como Rodar em Modo de Desenvolvimento
Siga as etapas abaixo para clonar e executar o projeto diretamente pelo código-fonte:

### 1. Clonar o Repositório
   ```bash
   git clone https://github.com/esdras1110/edook.git
   ```
   
### 2. Acessar o diretório do Projeto
   ```bash
   cd edook/frontend
   ```

### 3. Compilar e Executar
Execute o comando do Maven para baixar as dependências, compilar o ecossistema modular e iniciar a interface JavaFX:
   ```bash
   mvn clean javafx:run
   ```
---

## Geração de Instalador Nativo (Produção)
O ecossistema moderno do Java (a partir do Java 14+) possui a ferramenta jpackage, que permite empacotar a aplicação JavaFX junto com uma versão leve do JDK (via jlink) para que o usuário final não precise ter o Java instalado na máquina dele.

Para gerar um instalador residencial no Windows (.exe ou .msi), siga os passos abaixo:

### 1. Pré-requisitos Adicionais (Apenas para Windows)

O jpackage utiliza as ferramentas do WiX em segundo plano para construir o instalador. Baixe e instale o WiX Toolset e verifique se o diretório bin dele foi adicionado às variáveis de ambiente (PATH) do seu sistema.
**[Wix3.14.1](https://github.com/wixtoolset/wix3/releases/tag/wix3141rtm)**

### 2. Clonar o Repositório
   ```bash
   git clone https://github.com/esdras1110/edook.git
   ```

### 3. Acessar o diretório do Projeto
   ```bash
   cd edook/frontend
   ```

### 4. Compilar e Empacotar o .jar
Primeiro, gere o arquivo JAR limpo da aplicação através do Maven:
   ```bash
   mvn clean javafx:jlink
   ```

### 5. Executar o jpackage
Com o JAR gerado na pasta target, execute o comando abaixo no terminal para criar o instalador customizado:

   ```bash
   jpackage --type exe --name Edook --app-version 1.0.0 --runtime-image target/app --module com.edook.frontend/com.edook.frontend.HelloApplication --dest target/installer --win-shortcut --win-menu --win-dir-chooser --icon src/main/resources/com/edook/frontend/Images/"Icone do exe.ico"
   ```

### 6. Instalar
O wizard de instalação será criado no diretório target/installer, basta executá-lo para o cliente ser instalado no windows.
Pelo terminal:

   ```bash
   cd target/installer
   ./Edook-1.0.0.exe
   ```