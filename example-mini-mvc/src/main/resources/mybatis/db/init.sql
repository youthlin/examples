-- noinspection SqlNoDataSourceInspectionForFile
DROP TABLE IF EXISTS USERS;
CREATE TABLE
  USERS (
  id   INT,
  name VARCHAR(50)
);
INSERT INTO USERS (id, name) VALUES (1, 'LIN'), (2, 'Chen');
