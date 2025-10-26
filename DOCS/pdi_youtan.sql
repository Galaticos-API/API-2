-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: pdi_youtan
-- ------------------------------------------------------
-- Server version	8.0.43
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */
;

/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */
;

/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */
;

/*!50503 SET NAMES utf8 */
;

/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */
;

/*!40103 SET TIME_ZONE='+00:00' */
;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */
;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */
;

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */
;

/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */
;


CREATE DATABASE pdi_youtan;
USE pdi_youtan;

--
-- Table structure for table `avaliacao`
--
DROP TABLE IF EXISTS `avaliacao`;

/*!40101 SET @saved_cs_client     = @@character_set_client */
;

/*!50503 SET character_set_client = utf8mb4 */
;

CREATE TABLE `avaliacao` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único',
  `id_objetivo` int NOT NULL COMMENT 'FK para objetivo.id',
  `id_avaliador` int NOT NULL COMMENT 'FK para usuario.id',
  `nota` decimal(4, 2) NOT NULL COMMENT 'Nota de 0 a 10',
  `comentario` text NOT NULL COMMENT 'Texto da avaliação',
  `status_objetivo` varchar(50) NOT NULL COMMENT 'Ex: “Concluído”, “Abaixo do esperado”',
  `data_avaliacao` datetime NOT NULL COMMENT 'Data da avaliação',
  `criado_em` timestamp NOT NULL,
  `atualizado_em` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_objetivo_idx` (`id_objetivo`),
  KEY `id_avaliador_idx` (`id_avaliador`),
  CONSTRAINT `id_avaliador` FOREIGN KEY (`id_avaliador`) REFERENCES `usuario` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `id_objetivo` FOREIGN KEY (`id_objetivo`) REFERENCES `objetivo` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Table structure for table `documento`
--
DROP TABLE IF EXISTS `documento`;

/*!40101 SET @saved_cs_client     = @@character_set_client */
;

/*!50503 SET character_set_client = utf8mb4 */
;

CREATE TABLE `documento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pdi_id` int NOT NULL,
  `nome_arquivo` varchar(255) NOT NULL,
  `caminho_arquivo` varchar(255) NOT NULL,
  `data_upload` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tipo_documento` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pdi_id` (`pdi_id`),
  CONSTRAINT `documento_ibfk_1` FOREIGN KEY (`pdi_id`) REFERENCES `pdi` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Table structure for table `habilidade`
--
DROP TABLE IF EXISTS `habilidade`;

/*!40101 SET @saved_cs_client     = @@character_set_client */
;

/*!50503 SET character_set_client = utf8mb4 */
;

CREATE TABLE `habilidade` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `tipo_skill` enum('Hard Skill', 'Soft Skill') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nome` (`nome`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Table structure for table `objetivo`
--
DROP TABLE IF EXISTS `objetivo`;

/*!40101 SET @saved_cs_client     = @@character_set_client */
;

/*!50503 SET character_set_client = utf8mb4 */
;

CREATE TABLE `objetivo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pdi_id` int NOT NULL,
  `descricao` text NOT NULL,
  `prazo` date NOT NULL,
  `status` enum('Não Iniciado', 'Em Progresso', 'Concluído') NOT NULL,
  `comentarios` text,
  `peso` decimal(5, 2) DEFAULT NULL,
  `pontuacao` decimal(5, 2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pdi_id` (`pdi_id`),
  CONSTRAINT `objetivo_ibfk_1` FOREIGN KEY (`pdi_id`) REFERENCES `pdi` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Table structure for table `pdi`
--
DROP TABLE IF EXISTS `pdi`;

/*!40101 SET @saved_cs_client     = @@character_set_client */
;

/*!50503 SET character_set_client = utf8mb4 */
;

CREATE TABLE `pdi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `status` enum('Em Andamento', 'Concluído', 'Arquivado') NOT NULL,
  `data_criacao` date NOT NULL,
  `data_fechamento` date DEFAULT NULL,
  `pontuacao_geral` decimal(5, 2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `pdi_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 2 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Table structure for table `pdi_habilidade`
--
DROP TABLE IF EXISTS `pdi_habilidade`;

/*!40101 SET @saved_cs_client     = @@character_set_client */
;

/*!50503 SET character_set_client = utf8mb4 */
;

CREATE TABLE `pdi_habilidade` (
  `pdi_id` int NOT NULL,
  `habilidade_id` int NOT NULL,
  `nivel_avaliacao` int DEFAULT NULL,
  `data_avaliacao` date DEFAULT NULL,
  PRIMARY KEY (`pdi_id`, `habilidade_id`),
  KEY `habilidade_id` (`habilidade_id`),
  CONSTRAINT `pdi_habilidade_ibfk_1` FOREIGN KEY (`pdi_id`) REFERENCES `pdi` (`id`),
  CONSTRAINT `pdi_habilidade_ibfk_2` FOREIGN KEY (`habilidade_id`) REFERENCES `habilidade` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Table structure for table `usuario`
--
DROP TABLE IF EXISTS `usuario`;

/*!40101 SET @saved_cs_client     = @@character_set_client */
;

/*!50503 SET character_set_client = utf8mb4 */
;

CREATE TABLE `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `tipo_usuario` enum(
    'RH',
    'Gestor de Área',
    'Gestor Geral',
    'Colaborador'
  ) NOT NULL,
  `data_criacao` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('Ativo', 'Inativo') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE = InnoDB AUTO_INCREMENT = 2 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO
  usuario (
    nome,
    email,
    senha,
    tipo_usuario,
    status
  )
VALUES
  (
    'RH',
    'rh@rh',
    'tlOTLtJmZ+W5xkhPDmvtAA==',
    'RH',
    'Ativo'
  );
  
  select
  *
from
  usuario;

/*!40101 SET character_set_client = @saved_cs_client */
;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */
;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */
;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */
;

/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */
;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */
;

/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */
;

/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */
;

/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */
;

-- Dump completed on 2025-10-10 19:00:42