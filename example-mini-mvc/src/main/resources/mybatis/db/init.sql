-- noinspection SqlNoDataSourceInspectionForFile
CREATE TABLE IF NOT EXISTS
  USERS (
  id   INT,
  name VARCHAR(50)
);
INSERT INTO USERS (id, name) VALUES (1, 'LIN'), (2, 'Chen');
