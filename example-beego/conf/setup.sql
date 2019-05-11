CREATE TABLE IF NOT EXISTS `city`
(
    `id`   int          NOT NULL AUTO_INCREMENT,
    `pid`  int          NOT NULL DEFAULT '0',
    `code` varchar(255) NOT NULL DEFAULT '',
    `name` varchar(255) NOT NULL DEFAULT '',
    PRIMARY KEY (`id`),
    INDEX idx_name (name),
    INDEX idx_code (code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
