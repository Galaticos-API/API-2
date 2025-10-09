# Projeto API 2025-2 – Sistema de PDI (Plano de Desenvolvimento Individual)

## Índice

- [1. Descrição do Desafio](#1-descrição-do-desafio)
- [2. Backlog do Produto](#2-backlog-do-produto)
- [3. Cronograma de Evolução do Projeto](#3-cronograma-de-evolução-do-projeto)
- [4. Sprints](#4-sprints)
- [5. Tecnologias Utilizadas](#5-tecnologias-utilizadas)
- [6. Estrutura do Projeto](#6-estrutura-do-projeto)
- [7. Como Executar o Projeto](#7-como-executar-o-projeto)
- [8. Documentação](#8-documentação)
- [9. Equipe](#9-equipe)

---

## 1. Descrição do Desafio

### Parceiro

- **Empresa:** Youtan
- **Contato:** Fátima Marques Machado – [fatima.machado@youtan.com.br](mailto:fatima.machado@youtan.com.br)

### O problema principal

O Plano de Desenvolvimento Individual (PDI) é uma ferramenta essencial para o crescimento dos colaboradores, resultando em maior retenção de talentos, engajamento e aprimoramento de competências. Atualmente, o departamento de RH da Youtan gerencia esses planos através de **planilhas avulsas**, o que gera dificuldades no controle, falta de histórico consolidado e dificuldade na geração de relatórios e métricas de desempenho.

O objetivo deste projeto é desenvolver uma aplicação **Java Desktop com Banco de Dados** para centralizar e otimizar a gestão de PDIs, resolvendo os problemas atuais e apoiando o desenvolvimento dos colaboradores de forma estratégica.

---

## 2. Backlog do Produto

| Ranking | Prioridade | User Story | Estimativa (Horas) | Sprint |
| :--- | :--- | :--- | :--- | :--- |
| 1 | Alta | **US-01:** Como RH, quero cadastrar, consultar, editar e inativar usuários no sistema, para gerenciar quem tem acesso à plataforma. | 35 | Sprint 1 |
| 2 | Alta | **US-02:** Como RH, quero criar um novo PDI para um colaborador, associando-o a um ano específico, para iniciar o registro histórico dos planos. | 17 | Sprint 1 |
| 3 | Alta | **US-03:** Como RH ou Gestor de Área, quero definir objetivos e metas dentro de um PDI existente, para detalhar o que se espera do colaborador. | 16 | Sprint 1 |
| 4 | Média | **US-04:** Como RH ou Gestor de Área, quero avaliar as Hard Skills e Soft Skills de um colaborador dentro do seu PDI, para registrar suas competências. | A ser estimado | Sprint 2 |
| 5 | Média | **US-05:** Como RH ou Gestor de Área, quero atualizar o status de atingimento das metas de um PDI, para acompanhar o progresso do colaborador. | A ser estimado | Sprint 2 |
| 6 | Média | **US-06:** Como RH ou Gestor de Área, quero poder fazer o upload de documentos em um PDI específico, para centralizar as evidências de desenvolvimento. | A ser estimado | Sprint 2 |
| 7 | Baixa | **US-07:** Como Gestor Geral, quero visualizar um painel com o cálculo de atingimento de metas coletivo, para ter uma visão macro do desenvolvimento. | A ser estimado | Sprint 3 |
| 8 | Baixa | **US-08:** Como RH, quero poder exportar as informações de um PDI para uma planilha, para análises externas. | A ser estimado | Sprint 3 |
| 9 | Baixa | **US-09:** Como Gestor de Área, quero ter uma visualização consolidada do progresso dos PDIs de todos os meus liderados, para gerenciar minha equipe. | A ser estimado | Sprint 3 |


---

## 3. Cronograma de Evolução do Projeto

![Exemplo de Cronograma](./DOCS/MVP.png)

---

## 4. Sprints

| Fase                 | Previsão                | Status       |
| -------------------- | ----------------------- | ------------ |
| Kick Off Geral       | 25/08/2025 - 29/08/2025 | Concluído    |
| Sprint #1            | 08/09/2025 - 28/09/2025 | Concluído    |
| Sprint #2            | 06/10/2025 - 26/10/2025 | Em progresso |
| Sprint #3            | 03/11/2025 - 23/11/2025 | A começar    |
| Feira de Soluções    | 29/05/2025              | A começar    |
---

## 5. Tecnologias Utilizadas

- **Linguagem:** Java (Desktop com JavaFX)
- **Banco de Dados:** MySQL
- **Controle de Versão:** Git & GitHub
- **Ferramentas de Documentação:** User Stories, Wireframes, Diagramas (Classe e Entidade-Relacionamento)

---

## 6. Estrutura do Projeto

```/
├── src/
│ ├── main/
│ │ ├── java/
│ │ └── resources/
│ └── test/
├── docs/
├── .gitignore
└── README.md
```

---

## 7. Como Executar o Projeto

### Pré-requisitos

- Java JDK 25
- MySQL 8.0
- Apache Maven
- Git
- JavaFX 8.4

### Instalação e Execução

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/Galaticos-API/API-2.git
    ```
2.  **Configure o banco de dados:**
    - Execute o script `DOCS/script.sql` para criar o banco de dados e as tabelas.

3.  **Compile e execute o projeto**

---

## 8. Documentação

A documentação completa do projeto pode ser encontrada na pasta `/docs` do repositório ou através do link abaixo.

- **[Acessar Pasta de Documentação](./docs/)**

A pasta irá incluir (WIP):

- **Checklist de DoR (Definition of Ready) e DoD (Definition of Done)**
- **Detalhes de DoR e DoD por Sprint**
- **Estratégia de Branch (GitFlow)**
- **[Manual de Usuário](./docs/manual_usuario.md)**
- **[Manual de Instalação](./docs/manual_instalacao.md)**

---

## 9. Equipe

|           Nome            |     Função     |                                                                            GitHub                                                                             |                                                                                               Linkedin                                                                                                 |
| :-----------------------: | :------------: | :-----------------------------------------------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|     Emmanuel Garakis      | Product Owner  |    <a href='https://github.com/Garakis'><img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"></a>     | <a href='https://www.linkedin.com/in/emmanuel-basile-garakis-filho-024572266/'><img src='https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white'></a> |
|      Rafael Matesco       |  Scrum Master  |  <a href='https://github.com/RafaMatesco'><img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"></a>   | <a href='https://www.linkedin.com/in/rafael-giordano-matesco/'><img src='https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white'></a>                   |
|        Caio César         | Team Developer |    <a href='https://github.com/JkDeltaz'><img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"></a>    | <a href='https://www.linkedin.com/in/caio-c%C3%A9sar-santos-79976636a/'><img src='https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white'></a>       |
|        Daniel Dias        | Team Developer | <a href='https://github.com/DanielDPereira'><img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"></a> | <a href='https://www.linkedin.com/in/daniel-dias-pereira-40219425b/'><img src='https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white'></a>       |
|      Gabriel Lasaro       | Team Developer |   <a href='https://github.com/GaelNotFound'><img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"></a> | <a href='https://www.linkedin.com/in/gaelslasaro/'><img src='https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white'></a>                        |
|     Giovanni Moretto      | Team Developer |  <a href='https://github.com/Giomoret'><img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"></a>      | <a href='https://www.linkedin.com/in/giovanni-moretto-02b754271/'><img src='https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white'></a>       |
|       Gustavo Bueno       | Team Developer |  <a href='https://github.com/Darkghostly'><img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"></a>   | <a href='https://www.linkedin.com/in/gustavo-bueno-da-silva-797292324/'><img src='https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white'></a>       |
|     Gustavo Monteiro      | Team Developer | <a href='https://github.com/GustavoMGreco'><img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"></a>  | <a href='https://www.linkedin.com/in/gustavomgreco/'><img src='https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white'></a>                        |
