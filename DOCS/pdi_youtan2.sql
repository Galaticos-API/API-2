-- Remove o banco de dados se ele já existir (cuidado em produção!)
DROP DATABASE IF EXISTS `pdi_youtan`;

-- Cria o novo banco de dados
CREATE DATABASE `pdi_youtan`
/*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */
/*!80016 DEFAULT ENCRYPTION='N' */
;

-- Seleciona o banco de dados para usar
USE `pdi_youtan`;

-- Configurações gerais do MySQL (geralmente incluídas em dumps)
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

-- -----------------------------------------------------
-- Table `setor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `setor`;

CREATE TABLE `setor` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(150) NOT NULL COMMENT 'Nome do setor (Ex: "Desenvolvimento", "Recursos Humanos", "Financeiro")',
  `descricao` TEXT NULL COMMENT 'Descrição opcional do setor',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nome_UNIQUE` (`nome`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Tabela para armazenar os setores/áreas da empresa';

-- -----------------------------------------------------
-- Table `usuario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `usuario`;

CREATE TABLE `usuario` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `senha` VARCHAR(255) NOT NULL,
  `tipo_usuario` ENUM(
    'RH',
    'Gestor de Area',
    'Gestor Geral',
    'Colaborador'
  ) NOT NULL,
  `data_criacao` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` ENUM('Ativo', 'Inativo') NOT NULL,
  `setor_id` INT NULL COMMENT 'FK para setor.id, indica a qual setor o usuário pertence',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  INDEX `fk_usuario_setor_idx` (`setor_id` ASC) VISIBLE,
  CONSTRAINT `fk_usuario_setor` FOREIGN KEY (`setor_id`) REFERENCES `setor` (`id`) ON DELETE
  SET
    NULL ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `pdi`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `pdi`;

CREATE TABLE `pdi` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL COMMENT 'FK para usuario.id',
  `ano` INT NOT NULL COMMENT 'Ano de referência do PDI',
  `status` ENUM('Em Andamento', 'Concluído', 'Arquivado') NOT NULL,
  `data_criacao` DATE NOT NULL,
  `data_fechamento` DATE NULL DEFAULT NULL,
  `pontuacao_geral` DECIMAL(5, 2) NULL DEFAULT 0.00,
  PRIMARY KEY (`id`),
  INDEX `fk_pdi_usuario_idx` (`usuario_id` ASC) VISIBLE,
  CONSTRAINT `fk_pdi_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE -- Se o usuário for deletado, seus PDIs também são (ajuste se necessário)
  ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `objetivo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `objetivo`;

CREATE TABLE `objetivo` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `pdi_id` INT NOT NULL,
  `descricao` TEXT NOT NULL,
  `prazo` DATE NOT NULL,
  `status` ENUM('Não Iniciado', 'Em Progresso', 'Concluído') NOT NULL,
  `comentarios` TEXT NULL DEFAULT NULL,
  `peso` DECIMAL(5, 2) NULL DEFAULT NULL COMMENT 'Peso do objetivo no cálculo da pontuação geral do PDI',
  `pontuacao` DECIMAL(5, 2) NULL DEFAULT NULL COMMENT 'Pontuação/Progresso atual do objetivo',
  PRIMARY KEY (`id`),
  INDEX `fk_objetivo_pdi_idx` (`pdi_id` ASC) VISIBLE,
  CONSTRAINT `fk_objetivo_pdi` FOREIGN KEY (`pdi_id`) REFERENCES `pdi` (`id`) ON DELETE CASCADE -- Se o PDI for deletado, seus objetivos também são
  ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `avaliacao`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `avaliacao`;

CREATE TABLE `avaliacao` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Identificador único',
  `id_objetivo` INT NOT NULL COMMENT 'FK para objetivo.id',
  `id_avaliador` INT NOT NULL COMMENT 'FK para usuario.id (Quem avaliou)',
  `nota` DECIMAL(4, 2) NULL COMMENT 'Nota opcional de 0 a 10',
  `comentario` TEXT NULL COMMENT 'Texto da avaliação (pode ser opcional dependendo da regra)',
  `status_objetivo` VARCHAR(50) NULL COMMENT 'Ex: “Concluído”, “Abaixo do esperado” (Pode ser redundante com objetivo.status)',
  `data_avaliacao` DATETIME NOT NULL COMMENT 'Data e hora da avaliação',
  `criado_em` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `atualizado_em` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_avaliacao_objetivo_idx` (`id_objetivo` ASC) VISIBLE,
  INDEX `fk_avaliacao_avaliador_idx` (`id_avaliador` ASC) VISIBLE,
  CONSTRAINT `fk_avaliacao_objetivo` FOREIGN KEY (`id_objetivo`) REFERENCES `objetivo` (`id`) ON DELETE CASCADE -- Se o objetivo for deletado, suas avaliações também são
  ON UPDATE CASCADE,
  CONSTRAINT `fk_avaliacao_avaliador` -- Constraint corrigida para referenciar usuario
  FOREIGN KEY (`id_avaliador`) REFERENCES `usuario` (`id`) ON DELETE RESTRICT -- Impede deletar um usuário que fez avaliações
  ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `documento`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `documento`;

CREATE TABLE `documento` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `pdi_id` INT NOT NULL,
  `nome_arquivo` VARCHAR(255) NOT NULL,
  `caminho_arquivo` VARCHAR(255) NOT NULL COMMENT 'Pode ser um caminho relativo ou URL',
  `data_upload` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tipo_documento` VARCHAR(50) NULL DEFAULT NULL COMMENT 'Ex: Certificado, Feedback, Evidência',
  PRIMARY KEY (`id`),
  INDEX `fk_documento_pdi_idx` (`pdi_id` ASC) VISIBLE,
  CONSTRAINT `fk_documento_pdi` FOREIGN KEY (`pdi_id`) REFERENCES `pdi` (`id`) ON DELETE CASCADE -- Se o PDI for deletado, seus documentos também são
  ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `habilidade`;
DROP TABLE IF EXISTS `pdi_habilidade`;

-- Configurações finais do MySQL
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

INSERT INTO `setor` (`nome`) VALUES ('Desenvolvimento'), ('Recursos Humanos'), ('Financeiro');
INSERT INTO `usuario` (`nome`, `email`, `senha`, `tipo_usuario`, `status`, `setor_id`) VALUES ('Admin RH', 'rh@rh', 'tlOTLtJmZ+W5xkhPDmvtAA==', 'RH', 'Ativo', 2);