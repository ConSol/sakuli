-- delete alt DB
DROP TABLE sakuli.sakuli_steps;
DROP TABLE sakuli.sakuli_cases;
DROP TABLE sakuli.sakuli_suites;
DROP TABLE sakuli.sakuli_jobs;
DROP SCHEMA sakuli RESTRICT;

-- create new schema
CREATE SCHEMA sakuli;
CREATE TABLE sakuli.sakuli_cases
(
  id             INT                                 NOT NULL
  GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  sakuli_suites_id INT NOT NULL,
  caseID         VARCHAR(255)                        NOT NULL,
  result         INT                                 NOT NULL,
  result_desc    VARCHAR(45)                         NOT NULL,
  name           VARCHAR(255)                        NOT NULL,
  guid           VARCHAR(255)                        NOT NULL,
  start          VARCHAR(255)                        NOT NULL,
  stop           VARCHAR(255)                        NOT NULL,
  warning        INT,
  critical       INT,
  duration       REAL                                NOT NULL,
  lastpage       VARCHAR(255),
  screenshot     BLOB,
  msg            VARCHAR(2500),
  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id, sakuli_suites_id)
);
CREATE INDEX fk_sakuli_cases_sakuli_suites ON sakuli.sakuli_cases (sakuli_suites_id);
CREATE TABLE sakuli.sakuli_jobs
(
  id   INT PRIMARY KEY NOT NULL
  GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  guid VARCHAR(255)    NOT NULL
);
CREATE INDEX ind_guid ON sakuli.sakuli_jobs (guid);
CREATE TABLE sakuli.sakuli_steps
(
  id            INT                                 NOT NULL
  GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  sakuli_cases_id INT NOT NULL,
  result        INT                                 NOT NULL,
  result_desc   VARCHAR(45)                         NOT NULL,
  name          VARCHAR(255)                        NOT NULL,
  start         VARCHAR(255)                        NOT NULL,
  stop          VARCHAR(255)                        NOT NULL,
  warning       INT,
  duration      REAL                                NOT NULL,
  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id, sakuli_cases_id)
);
CREATE INDEX fk_sakuli_steps_sakuli_cases1 ON sakuli.sakuli_steps (sakuli_cases_id);
CREATE TABLE sakuli.sakuli_suites
(
  id          INT PRIMARY KEY                     NOT NULL
  GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  suiteID     VARCHAR(255)                        NOT NULL,
  result      INT                                 NOT NULL,
  result_desc VARCHAR(45)                         NOT NULL,
  name        VARCHAR(255)                        NOT NULL,
  guid        VARCHAR(255)                        NOT NULL,
  start       VARCHAR(255)                        NOT NULL,
  stop        VARCHAR(255),
  warning     INT,
  critical    INT,
  duration    REAL,
  browser     VARCHAR(255),
  host        VARCHAR(255),
  screenshot  BLOB,
  msg         VARCHAR(2500),
  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX guid_UNIQUE ON sakuli.sakuli_suites (guid);
