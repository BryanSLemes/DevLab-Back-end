# DevLab-Back-end

[![NPM](https://img.shields.io/npm/l/react)](https://github.com/BryanSLemes/DevLab-Back-end/blob/master/LICENSE)

> Status: Em desenvolvimento 👨‍💻

# Sobre o projeto
O projeto consiste no desenvolvimento de um site/plataforma de jogos online. Alguns jogos que foram ou serão implementados serão: Jogo da Velha, Xadrez, Dama, Campo minado, entre outros que serão adicionados ao longo do tempo.


<!--
  Sobre o projeto:
  explicar sobre o projeto de forma clara e concisa oque é esse projeto, em que situação eu fiz o projeto, qual o propósito do projeto...

Opções de jogos:
  Jogo da Velha...
  ...
-->

## Tecnologias

- Java
- Spring Boot
- Spring Data MongoDB
- JWT (JSON Web Token) para autenticação
- Maven

## Implantação em produção
<!-- - Back-end: Heroku colocar dps
- Front-end: Netlify -->
- Banco de Dados: MongoDB

## Desenvolvedores
  * [Bryan Lemes](https://github.com/bryanslemes)
  * [Antônio Martins](https://github.com/AntonioMartinss)

## UX & UI Designer
  * [Henrique Pimentel](https://www.behance.net/HenriquePimentelCs)

<br>

# Como Executar o projeto

## Banco de Dados
  Pré-requisito: [Baixar o MongoDB Community](https://www.mongodb.com/try/download/community)
  
  ### Acesse Mongo Shell ou MongoDB Compass:

  ```bash
  # criar ou selecionar banco de dados
  use DevLab_DB

  # criar collection
  db.createCollection("usuarios")
  ```

<br>

## Front-end
  *  [Link do Repositório](https://github.com/AntonioMartinss/DevLab_front_end)

## Back-end (este repositório)
  Pré-requisito: Java 17

  ```bash
  # clonar repositório
  git clone https://github.com/BryanSLemes/DevLab-Back-end

  # entrar na pasta do projeto
  cd DevLab-Back-End
  
  # executar o projeto
  ./mvnw spring-boot:run
  ```
