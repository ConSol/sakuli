-- delete alt DB
DROP TABLE sahi.sahi_steps;
DROP TABLE sahi.sahi_cases;
DROP TABLE sahi.sahi_suites;
DROP TABLE sahi.sahi_jobs;
DROP SCHEMA sahi RESTRICT;

-- create new schema
CREATE SCHEMA sahi;
CREATE TABLE sahi.sahi_cases
(
  id             INT                                 NOT NULL
  GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  sahi_suites_id INT                                 NOT NULL,
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
  PRIMARY KEY (id, sahi_suites_id)
);
CREATE INDEX fk_sahi_cases_sahi_suites ON sahi.sahi_cases (sahi_suites_id);
CREATE TABLE sahi.sahi_jobs
(
  id   INT PRIMARY KEY NOT NULL
  GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  guid VARCHAR(255)    NOT NULL
);
CREATE INDEX ind_guid ON sahi.sahi_jobs (guid);
CREATE TABLE sahi.sahi_steps
(
  id            INT                                 NOT NULL
  GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  sahi_cases_id INT                                 NOT NULL,
  result        INT                                 NOT NULL,
  result_desc   VARCHAR(45)                         NOT NULL,
  name          VARCHAR(255)                        NOT NULL,
  start         VARCHAR(255)                        NOT NULL,
  stop          VARCHAR(255)                        NOT NULL,
  warning       INT,
  duration      REAL                                NOT NULL,
  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id, sahi_cases_id)
);
CREATE INDEX fk_sahi_steps_sahi_cases1 ON sahi.sahi_steps (sahi_cases_id);
CREATE TABLE sahi.sahi_suites
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
CREATE UNIQUE INDEX guid_UNIQUE ON sahi.sahi_suites (guid);
