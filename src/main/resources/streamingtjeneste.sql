-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema streamingtjeneste
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema streamingtjeneste
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `streamingtjeneste` DEFAULT CHARACTER SET utf8mb3 ;
USE `streamingtjeneste` ;

-- -----------------------------------------------------
-- Table `streamingtjeneste`.`movie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `streamingtjeneste`.`movie` (
  `movieid` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NULL DEFAULT NULL,
  `director` VARCHAR(100) NULL DEFAULT NULL,
  `releasedate` DATE NULL DEFAULT NULL,
  `genre` VARCHAR(100) NULL DEFAULT NULL,
  `description` VARCHAR(500) NULL DEFAULT NULL,
  `rating` INT NULL DEFAULT NULL,
  `src` VARCHAR(255) NULL DEFAULT NULL,
  `imgsrc` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`movieid`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `streamingtjeneste`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `streamingtjeneste`.`user` (
  `iduser` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NULL DEFAULT NULL,
  `email` VARCHAR(255) NULL DEFAULT NULL,
  `password` VARCHAR(255) NULL DEFAULT NULL,
  `birthdate` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`iduser`))
ENGINE = InnoDB
AUTO_INCREMENT = 16
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `streamingtjeneste`.`favorites`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `streamingtjeneste`.`favorites` (
  `idfavorites` INT NOT NULL AUTO_INCREMENT,
  `fkuser` INT NULL DEFAULT NULL,
  `fkmovie` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idfavorites`),
  INDEX `fkuser_idx` (`fkuser` ASC) VISIBLE,
  INDEX `fkmovie_idx` (`fkmovie` ASC) VISIBLE,
  CONSTRAINT `fkmovie`
    FOREIGN KEY (`fkmovie`)
    REFERENCES `streamingtjeneste`.`movie` (`movieid`),
  CONSTRAINT `fkuser`
    FOREIGN KEY (`fkuser`)
    REFERENCES `streamingtjeneste`.`user` (`iduser`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 13
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `streamingtjeneste`.`review`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `streamingtjeneste`.`review` (
  `idreview` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NULL DEFAULT NULL,
  `author` VARCHAR(100) NULL DEFAULT NULL,
  `authorid` INT NULL DEFAULT NULL,
  `content` VARCHAR(1000) NULL DEFAULT NULL,
  `rating` INT NULL DEFAULT NULL,
  `date` DATE NULL DEFAULT NULL,
  `fkmovie` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idreview`),
  INDEX `movie_idx` (`fkmovie` ASC) VISIBLE,
  INDEX `author_idx` (`authorid` ASC) VISIBLE,
  CONSTRAINT `author`
    FOREIGN KEY (`authorid`)
    REFERENCES `streamingtjeneste`.`user` (`iduser`),
  CONSTRAINT `movie`
    FOREIGN KEY (`fkmovie`)
    REFERENCES `streamingtjeneste`.`movie` (`movieid`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 38
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `streamingtjeneste`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `streamingtjeneste`.`roles` (
  `idroles` INT NOT NULL AUTO_INCREMENT,
  `role` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`idroles`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `streamingtjeneste`.`user_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `streamingtjeneste`.`user_roles` (
  `iduser_roles` INT NOT NULL AUTO_INCREMENT,
  `fkuser` INT NULL DEFAULT NULL,
  `fkrole` INT NULL DEFAULT NULL,
  PRIMARY KEY (`iduser_roles`),
  INDEX `user_idx` (`fkuser` ASC) VISIBLE,
  INDEX `role_idx` (`fkrole` ASC) VISIBLE,
  CONSTRAINT `role`
    FOREIGN KEY (`fkrole`)
    REFERENCES `streamingtjeneste`.`roles` (`idroles`),
  CONSTRAINT `user`
    FOREIGN KEY (`fkuser`)
    REFERENCES `streamingtjeneste`.`user` (`iduser`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 19
DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
