# Sistema de Gerenciamento de Livros e Autores - Challenge LiterAlura 

## Descrição
O projeto **Literalura** é um sistema back-end simples desenvolvido em **Java** com **Spring Boot** para gerenciamento de livros e autores. O sistema oferece funcionalidades como:
- Pesquisa de livros por título.
- Cadastro de livros e autores.
- Listagem de livros e autores registrados.
- Listagem de liros por idiomas.
- Estatísticas de downloads de livros.
- Exibição dos 10 livros mais baixados.

Este sistema é integrado com uma **API externa** para obter dados sobre livros, e utiliza um **banco de dados PostgreSQL** para armazenar informações sobre livros, autores e downloads.

## Funcionalidades

- **Pesquisa de livros por título**: Busca de livros em uma API externa e cadastro no banco de dados.
- **Cadastro de livros e autores**: Adiciona livros e seus respectivos autores ao sistema.
- **Listagem de livros e autores registrados**: Exibe todos os livros e autores presentes no banco de dados.
- **Pesquisa de autores vivos em um ano específico**: Exibe os autores que estavam vivos em um determinado ano.
- **Pesquisa de livros em um idioma específico**: Exibe livros registrados em um idioma específico.
- **Geração de estatísticas de downloads**: Utiliza a classe `DoubleSummaryStatistics` para gerar estatísticas de downloads dos livros.
- **Top 10 livros mais baixados**: Exibe os 10 livros mais baixados, com base no número de downloads registrados no sistema.

## Tecnologias Utilizadas

- **Java**: Linguagem de programação principal.
- **Spring Boot**: Framework para desenvolvimento de aplicações Java.
- **Spring Data JPA / Hibernate**: Para interação com o banco de dados PostgreSQL.
- **PostgreSQL**: Banco de dados relacional utilizado para armazenar as informações dos livros e autores.
- **APIs Externas**: Utilização da API **Gutendex** para busca de livros.
- **Maven**: Gerenciador de dependências.
- **Lombok**: Para redução de código boilerplate.

## Estrutura do Projeto

A estrutura do projeto é organizada nos seguintes pacotes:

- **br.com.alura.literalura.model**: Contém as entidades do sistema (Livro, Autor, etc.).
- **br.com.alura.literalura.repository**: Repositórios do Spring Data JPA para interação com o banco de dados.
- **br.com.alura.literalura.service**: Serviços para consumo de API e conversão de dados.
- **br.com.alura.literalura.principal**: Classe principal que contém a lógica de interação com o usuário via terminal.

## Como Executar

1. Clone o repositório:
   git clone [[git@github.com:Lumabarbosa/challenge_literalura.git](https://github.com/Lumabarbosa/challenge_literalura.git)]

2. Acesse o diretório do projeto através do terminal:
  cd challengeliteralura
  
4. Configure as credenciais de acesso ao banco de dados PostgreSQL no arquivo application.properties.
   
6. Execute o projeto usando Maven:
  mvn spring-boot:run

8. Acesse o sistema através do terminal e interaja com as funcionalidades.

## Captura de tela com aplicação funcionando:
https://github.com/Lumabarbosa/challenge_literalura/blob/main/Screen%20recording%201%20(online-video-cutter.com).mp4

