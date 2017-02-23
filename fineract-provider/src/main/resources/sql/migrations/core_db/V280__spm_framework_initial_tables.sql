CREATE TABLE `m_surveys` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`a_key` VARCHAR(32) NOT NULL,
	`a_name` VARCHAR(255) NOT NULL,
	`description` VARCHAR(4000) NULL,
	`country_code` VARCHAR(2) NOT NULL,
	`valid_from` DATETIME NULL DEFAULT NULL,
	`valid_to` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `m_survey_components` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `survey_id` BIGINT(20) NOT NULL,
  `a_key` VARCHAR(32) NOT NULL,
  `a_text` VARCHAR(255) NOT NULL,
  `description` VARCHAR(4000) NULL,
  `sequence_no` INT(4) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`survey_id`) REFERENCES `m_surveys` (`id`)
);

CREATE TABLE `m_survey_questions` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `survey_id` BIGINT(20) NOT NULL,
  `component_key` VARCHAR(32) NULL,
  `a_key` VARCHAR(32) NOT NULL,
  `a_text` VARCHAR(255) NOT NULL,
  `description` VARCHAR(4000) NULL,
  `sequence_no` INT(4) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`survey_id`) REFERENCES `m_surveys` (`id`)
);

CREATE TABLE `m_survey_responses` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `question_id` BIGINT(20) NOT NULL,
  `a_text` VARCHAR(255) NOT NULL,
  `a_value` INT(4) NOT NULL,
  `sequence_no` INT(4) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`question_id`) REFERENCES `m_survey_questions` (`id`)
);

CREATE TABLE `m_survey_lookup_tables` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `survey_id` BIGINT(20) NOT NULL,
  `a_key` VARCHAR(255) NOT NULL,
  `description` INT(4) NULL,
  `value_from` INT(4) NOT NULL,
  `value_to` INT(4) NOT NULL,
  `score` DECIMAL(5, 2) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`survey_id`) REFERENCES `m_surveys` (`id`)
);

CREATE TABLE `m_survey_scorecards` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `survey_id` BIGINT(20) NOT NULL,
  `question_id` BIGINT(20) NOT NULL,
  `response_id` BIGINT(20) NOT NULL,
  `staff_id` BIGINT(20) NOT NULL,
  `client_id` BIGINT(20) NOT NULL,
  `created_on` DATETIME NULL DEFAULT NULL,
  `a_value` INT(4) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`survey_id`) REFERENCES `m_surveys` (`id`),
  FOREIGN KEY (`question_id`) REFERENCES `m_survey_questions` (`id`),
  FOREIGN KEY (`response_id`) REFERENCES `m_survey_responses` (`id`),
  FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`),
  FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);