-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pdi_youtan
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE pdi_youtan;
USE pdi_youtan;

--
-- Table structure for table `avaliacao`
--

DROP TABLE IF EXISTS `avaliacao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `avaliacao` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Identificador único',
  `id_objetivo` int NOT NULL COMMENT 'FK para objetivo.id',
  `id_avaliador` int NOT NULL COMMENT 'FK para usuario.id (Quem avaliou)',
  `nota` decimal(4,2) DEFAULT NULL COMMENT 'Nota opcional de 0 a 10',
  `comentario` text COMMENT 'Texto da avaliação (pode ser opcional dependendo da regra)',
  `status_objetivo` varchar(50) DEFAULT NULL COMMENT 'Ex: “Concluído”, “Abaixo do esperado” (Pode ser redundante com objetivo.status)',
  `data_avaliacao` datetime NOT NULL COMMENT 'Data e hora da avaliação',
  `criado_em` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `atualizado_em` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_avaliacao_objetivo_idx` (`id_objetivo`),
  KEY `fk_avaliacao_avaliador_idx` (`id_avaliador`),
  CONSTRAINT `fk_avaliacao_avaliador` FOREIGN KEY (`id_avaliador`) REFERENCES `usuario` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_avaliacao_objetivo` FOREIGN KEY (`id_objetivo`) REFERENCES `objetivo` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `avaliacao`
--

LOCK TABLES `avaliacao` WRITE;
/*!40000 ALTER TABLE `avaliacao` DISABLE KEYS */;
INSERT INTO `avaliacao` VALUES (1,5,1,10.00,'Hard skill','Concluído','2025-10-28 17:45:43','2025-10-28 20:45:43',NULL),(2,5,1,0.50,'Hard skill','Concluído','2025-10-28 17:47:49','2025-10-28 20:47:49',NULL),(3,5,1,2.00,'Hard skill','Concluído','2025-10-28 17:50:06','2025-10-28 20:50:06',NULL),(4,5,1,0.50,'Hard skill','Concluído','2025-10-28 17:53:20','2025-10-28 20:53:20',NULL),(5,5,1,0.50,'Hard skill','Concluído','2025-10-28 17:54:21','2025-10-28 20:54:21',NULL),(6,5,1,1.00,'Hard skill','Concluído','2025-10-28 17:55:23','2025-10-28 20:55:23',NULL),(7,5,1,1.00,'Hard skill','Concluído','2025-10-28 18:00:28','2025-10-28 21:00:28',NULL),(8,7,1,6.00,'Soft Skill','Concluído','2025-10-28 18:02:18','2025-10-28 21:02:18',NULL);
/*!40000 ALTER TABLE `avaliacao` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `documento`
--

DROP TABLE IF EXISTS `documento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `documento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pdi_id` int NOT NULL,
  `nome_arquivo` varchar(255) NOT NULL,
  `caminho_arquivo` varchar(255) NOT NULL COMMENT 'Pode ser um caminho relativo ou URL',
  `data_upload` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tipo_documento` varchar(50) DEFAULT NULL COMMENT 'Ex: Certificado, Feedback, Evidência',
  PRIMARY KEY (`id`),
  KEY `fk_documento_pdi_idx` (`pdi_id`),
  CONSTRAINT `fk_documento_pdi` FOREIGN KEY (`pdi_id`) REFERENCES `pdi` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `documento`
--

LOCK TABLES `documento` WRITE;
/*!40000 ALTER TABLE `documento` DISABLE KEYS */;
/*!40000 ALTER TABLE `documento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `objetivo`
--

DROP TABLE IF EXISTS `objetivo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `objetivo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pdi_id` int NOT NULL,
  `descricao` text NOT NULL,
  `prazo` date NOT NULL,
  `status` enum('Não Iniciado','Em Progresso','Concluído') NOT NULL,
  `comentarios` text,
  `peso` decimal(5,2) DEFAULT NULL COMMENT 'Peso do objetivo no cálculo da pontuação geral do PDI',
  `pontuacao` decimal(5,2) DEFAULT NULL COMMENT 'Pontuação/Progresso atual do objetivo',
  PRIMARY KEY (`id`),
  KEY `fk_objetivo_pdi_idx` (`pdi_id`),
  CONSTRAINT `fk_objetivo_pdi` FOREIGN KEY (`pdi_id`) REFERENCES `pdi` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `objetivo`
--

LOCK TABLES `objetivo` WRITE;
/*!40000 ALTER TABLE `objetivo` DISABLE KEYS */;
INSERT INTO `objetivo` VALUES (5,5,'Curso hard','2025-11-30','Concluído','Hard skill',1.00,1.00),(6,5,'Curso soft','2025-11-30','Não Iniciado','Soft Skill',1.00,1.00),(7,5,'Objetivo diferente','2026-01-01','Concluído','Soft Skill',1.00,1.00);
/*!40000 ALTER TABLE `objetivo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pdi`
--

DROP TABLE IF EXISTS `pdi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pdi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL COMMENT 'FK para usuario.id',
  `status` enum('Em Andamento','Concluído','Arquivado') NOT NULL,
  `data_criacao` date NOT NULL,
  `data_fechamento` date DEFAULT NULL,
  `pontuacao_geral` decimal(5,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `fk_pdi_usuario_idx` (`usuario_id`),
  CONSTRAINT `fk_pdi_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pdi`
--

LOCK TABLES `pdi` WRITE;
/*!40000 ALTER TABLE `pdi` DISABLE KEYS */;
INSERT INTO `pdi` VALUES (5,4,'Em Andamento','2025-10-28','2026-11-30',0.67);
/*!40000 ALTER TABLE `pdi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `setor`
--

DROP TABLE IF EXISTS `setor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `setor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) NOT NULL COMMENT 'Nome do setor (Ex: "Desenvolvimento", "Recursos Humanos", "Financeiro")',
  `descricao` text COMMENT 'Descrição opcional do setor',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nome_UNIQUE` (`nome`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabela para armazenar os setores/áreas da empresa';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `setor`
--

LOCK TABLES `setor` WRITE;
/*!40000 ALTER TABLE `setor` DISABLE KEYS */;
INSERT INTO `setor` VALUES (1,'Desenvolvimento',NULL),(2,'Recursos Humanos',NULL),(3,'Financeiro',NULL);
/*!40000 ALTER TABLE `setor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `tipo_usuario` enum('RH','Gestor de Area','Gestor Geral','Colaborador') NOT NULL,
  `data_criacao` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('Ativo','Inativo') NOT NULL,
  `setor_id` varchar(255) DEFAULT NULL COMMENT 'FK para setor.id, indica a qual setor o usuário pertence',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_usuario_setor_idx` (`setor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Administrador','rh@rh','tlOTLtJmZ+W5xkhPDmvtAA==','RH','2025-10-26 18:42:40','Ativo','2'),(2,'jose','jose@gmail','TibcoFEoMXhhu4Yjfb5Emw==','Gestor de Area','2025-10-26 19:13:43','Ativo','1'),(3,'maria','maria@gmail','S9EDa2ghgfvTKii/HkIevw==','Gestor Geral','2025-10-26 19:14:04','Ativo','1'),(4,'Rafael Giordano Matesco','rafael@gmail','PmDQlKecs/3N1m9ATjODkA==','Colaborador','2025-10-26 19:14:21','Ativo','1'),(6,'ronaldo','ronaldo@gmail','9JCExWi/TPtxNCkbJ4chCg==','Gestor de Area','2025-10-26 20:00:46','Ativo','3');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-28 18:04:04
