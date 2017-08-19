-- noinspection SqlNoDataSourceInspectionForFile
-- DROP TABLE IF EXISTS USERS;
CREATE TABLE IF NOT EXISTS
  USERS (
  id    BIGINT PRIMARY KEY AUTO_INCREMENT,
  name  VARCHAR(50),
  email VARCHAR(50),
  note  VARCHAR(255)
);
