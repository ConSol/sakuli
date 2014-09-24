SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL';

DROP SCHEMA IF EXISTS `sahi`;
CREATE SCHEMA IF NOT EXISTS `sahi`
  DEFAULT CHARACTER SET utf8;
USE `sahi`;

-- -----------------------------------------------------
-- Table `sahi`.`sahi_suites`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sahi`.`sahi_suites` (
  `id`          INT(11)       NOT NULL AUTO_INCREMENT,
  `suiteID`     VARCHAR(255)  NOT NULL,
  `result`      INT(11)       NOT NULL,
  `result_desc` VARCHAR(45)   NOT NULL,
  `name`        VARCHAR(255)  NOT NULL,
  `guid`        VARCHAR(255)  NOT NULL,
  `start`       VARCHAR(255)  NOT NULL,
  `stop`        VARCHAR(255)  NULL,
  `warning`     INT(11)       NULL,
  `critical`    INT(11)       NULL,
  `duration`    FLOAT         NULL,
  `browser`     VARCHAR(255)  NULL,
  `host`        VARCHAR(255)  NULL,
  `screenshot`  MEDIUMBLOB    NULL,
  `msg`         VARCHAR(2500) NULL,
  `time`        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `guid_UNIQUE` (`guid` ASC))
  ENGINE = MyISAM
  DEFAULT CHARACTER SET = utf8
  COMMENT = 'Sahi Testcases';


-- -----------------------------------------------------
-- Table `sahi`.`sahi_cases`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sahi`.`sahi_cases` (
  `id`             INT(11)       NOT NULL AUTO_INCREMENT,
  `sahi_suites_id` INT(11)       NOT NULL,
  `caseID`         VARCHAR(255)  NOT NULL,
  `result`         INT(11)       NOT NULL,
  `result_desc`    VARCHAR(45)   NOT NULL,
  `name`           VARCHAR(255)  NOT NULL,
  `guid`           VARCHAR(255)  NOT NULL,
  `start`          VARCHAR(255)  NOT NULL,
  `stop`           VARCHAR(255)  NOT NULL,
  `warning`        INT(11)       NULL DEFAULT NULL,
  `critical`       INT(11)       NULL DEFAULT NULL,
  `duration`       FLOAT         NOT NULL,
  `lastpage`       VARCHAR(1000)  NULL DEFAULT NULL,
  `screenshot`     MEDIUMBLOB    NULL DEFAULT NULL,
  `msg`            VARCHAR(2500) NULL DEFAULT NULL,
  `time`           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`, `sahi_suites_id`),
  INDEX `fk_sahi_cases_sahi_suites` (`sahi_suites_id` ASC))
  ENGINE = MyISAM
  DEFAULT CHARACTER SET = utf8
  COMMENT = 'Sahi Testcases';


-- -----------------------------------------------------
-- Table `sahi`.`sahi_jobs`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sahi`.`sahi_jobs` (
  `id`   INT(11)      NOT NULL AUTO_INCREMENT,
  `guid` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ind_guid` (`guid` ASC))
  ENGINE = MyISAM
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `sahi`.`sahi_steps`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sahi`.`sahi_steps` (
  `id`            INT(11)      NOT NULL AUTO_INCREMENT,
  `sahi_cases_id` INT(11)      NOT NULL,
  `result`        INT(11)      NOT NULL,
  `result_desc`   VARCHAR(45)  NOT NULL,
  `name`          VARCHAR(255) NOT NULL,
  `start`         VARCHAR(255) NOT NULL,
  `stop`          VARCHAR(255) NOT NULL,
  `warning`       INT(11)      NULL DEFAULT NULL,
  `duration`      FLOAT        NOT NULL,
  `time`          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`, `sahi_cases_id`),
  INDEX `fk_sahi_steps_sahi_cases1` (`sahi_cases_id` ASC))
  ENGINE = MyISAM
  DEFAULT CHARACTER SET = utf8
  COMMENT = 'Sahi Testcases';


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
