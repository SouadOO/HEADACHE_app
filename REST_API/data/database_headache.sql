CREATE DATABASE headache_app;
 
USE headache_app;

CREATE TABLE IF NOT EXISTS `Users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password_hash` text NOT NULL,
  `user_type` ENUM('A', 'P', 'D') NOT NULL,
  `name` varchar(250) NOT NULL,
  `sex` ENUM('M', 'F') DEFAULT NULL,
  `age` int(3) DEFAULT NULL,
  `my_doctor_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  FOREIGN KEY (my_doctor_id) REFERENCES `Users`(`user_id`),
  UNIQUE KEY `email` (`email`)
);
 
CREATE TABLE IF NOT EXISTS `Attacks` (
  `attack_id` int(11) NOT NULL AUTO_INCREMENT,
  `status` ENUM('stopped', 'in_progress') NOT NULL,
  `duration` int(11) DEFAULT NULL,
  `intensity` ENUM('Mild', 'Moderate', 'Intense') NOT NULL,
  `patient_id` int(11) NOT NULL,
  `notes`  text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `started_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `stopped_at` timestamp  DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`attack_id`),
  FOREIGN KEY (`patient_id`) REFERENCES `Users`(`user_id`)
);

CREATE TABLE IF NOT EXISTS `Symptoms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) DEFAULT NULL,
  `patient_id` int(11),
  `icon` BLOB,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  FOREIGN KEY (`patient_id`) REFERENCES `Users`(`user_id`)
);

CREATE TABLE IF NOT EXISTS `AuraSymptoms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) DEFAULT NULL,
  `patient_id` int(11),
  `icon` BLOB,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  FOREIGN KEY (`patient_id`) REFERENCES  `Users`(`user_id`)

);

CREATE TABLE IF NOT EXISTS `Triggers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) DEFAULT NULL,
  `patient_id` int(11),
  `icon` BLOB,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  FOREIGN KEY (`patient_id`) REFERENCES  `Users`(`user_id`)
);

CREATE TABLE IF NOT EXISTS `Positions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) DEFAULT NULL,
  `patient_id` int(11),
  `icon` BLOB,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  FOREIGN KEY (`patient_id`) REFERENCES  `Users`(`user_id`)
);

CREATE TABLE IF NOT EXISTS `Meddications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) DEFAULT NULL,
  `patient_id` int(11),
  `icon` BLOB,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  FOREIGN KEY (`patient_id`) REFERENCES  `Users`(`user_id`)
);


CREATE TABLE IF NOT EXISTS `Attack_Symptoms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attack_id` int(11) NOT NULL,
  `symptom_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`attack_id`) REFERENCES `Attacks`(`attack_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`symptom_id`) REFERENCES `Symptoms`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `Attack_AuraSymptoms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attack_id` int(11) NOT NULL,
  `aura_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`attack_id`) REFERENCES `Attacks`(`attack_id`)  ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`aura_id`) REFERENCES `AuraSymptoms`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `Attack_Triggers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attack_id` int(11) NOT NULL,
  `trigers_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`attack_id`) REFERENCES `Attacks`(`attack_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`trigers_id`) REFERENCES `Triggers`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `Attack_Meddications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attack_id` int(11) NOT NULL,
  `meddication_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`attack_id`) REFERENCES `Attacks`(`attack_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`meddication_id`) REFERENCES `Triggers`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `Attack_Positions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attack_id` int(11) NOT NULL,
  `position_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`attack_id`) REFERENCES `Attacks`(`attack_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`position_id`) REFERENCES `Positions`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `VerificationCode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `experation` INT NOT NULL DEFAULT 2,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
);


