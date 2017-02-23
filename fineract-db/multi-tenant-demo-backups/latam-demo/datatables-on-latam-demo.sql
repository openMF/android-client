DROP TABLE IF EXISTS `latam_extra_client_details`;
CREATE TABLE `latam_extra_client_details` (
  `client_id` bigint(20) NOT NULL,
  `Business Description` varchar(100) DEFAULT NULL,
  `Years in Business` int(11) DEFAULT NULL,
  `Gender_cd` int(11) DEFAULT NULL,
  `Education_cv` varchar(60) DEFAULT NULL,
  `Next Visit` date DEFAULT NULL,
  `Highest Rate Paid` decimal(19,6) DEFAULT NULL,
  `Comment` text,
  PRIMARY KEY (`client_id`),
  CONSTRAINT `FK_latam_extra_client_details` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `latam_family_details`;
CREATE TABLE `latam_family_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `Name` varchar(40) DEFAULT NULL,
  `Date of Birth` date DEFAULT NULL,
  `Points Score` int(11) DEFAULT NULL,
  `Education_cd_Highest` int(11) DEFAULT NULL,
  `Other Notes` text,
  PRIMARY KEY (`id`),
  KEY `FK_Extra Family Details Data_1` (`client_id`),
  CONSTRAINT `FK_latam_family_details` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `latam_extra_loan_details`;
CREATE TABLE `latam_extra_loan_details` (
  `loan_id` bigint(20) NOT NULL,
  `Business Description` varchar(100) DEFAULT NULL,
  `Years in Business` int(11) DEFAULT NULL,
  `Gender_cd` int(11) DEFAULT NULL,
  `Education_cv` varchar(60) DEFAULT NULL,
  `Next Visit` date DEFAULT NULL,
  `Highest Rate Paid` decimal(19,6) DEFAULT NULL,
  `Comment` text,
  PRIMARY KEY (`loan_id`),
  CONSTRAINT `FK_latam_extra_loan_details` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `mifostenant-latam`.`m_code` (`code_name`, `is_system_defined`) VALUES ('Gender', 1);
INSERT INTO `mifostenant-latam`.`m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (2, 'Male', 1); INSERT INTO `mifostenant-latam`.`m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (2, 'Female', 2);


INSERT INTO `mifostenant-latam`.`m_code` (`code_name`, `is_system_defined`) VALUES ('Education', 1);
INSERT INTO `mifostenant-latam`.`m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (3, 'Primary', 1); INSERT INTO `mifostenant-latam`.`m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (3, 'Secondary', 2); INSERT INTO `mifostenant-latam`.`m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (3, 'University', 3);