

DROP TABLE IF EXISTS `bankin_acct`;
CREATE TABLE `bankin_acct` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `amt` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

INSERT INTO `bankin_acct` VALUES ('1', '0');


DROP TABLE IF EXISTS `bankin_order`;
CREATE TABLE `bankin_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `transfer_number` varchar(200) DEFAULT NULL,
  `status` varchar(200) DEFAULT NULL,
  `amt` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=880 DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `bankout_acct`;
CREATE TABLE `bankout_acct` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `amt` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;


INSERT INTO `bankout_acct` VALUES ('1', '10000000');


DROP TABLE IF EXISTS `bankout_order`;
CREATE TABLE `bankout_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `transfer_number` varchar(200) DEFAULT NULL,
  `status` varchar(200) DEFAULT NULL,
  `amt` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1392 DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `score_acct`;
CREATE TABLE `score_acct` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bank_out_account_id` bigint(20) DEFAULT NULL,
  `bank_in_account_id` bigint(20) DEFAULT NULL,
  `score` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;


INSERT INTO `score_acct` VALUES ('1', null, '1', '0');
INSERT INTO `score_acct` VALUES ('2', '1', null, '0');


DROP TABLE IF EXISTS `score_incr_order`;
CREATE TABLE `score_incr_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(20) DEFAULT NULL,
  `bank_account_type` varchar(200) DEFAULT NULL,
  `incr_order_number` varchar(200) DEFAULT NULL,
  `status` varchar(200) DEFAULT NULL,
  `score` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1938 DEFAULT CHARSET=utf8mb4;

