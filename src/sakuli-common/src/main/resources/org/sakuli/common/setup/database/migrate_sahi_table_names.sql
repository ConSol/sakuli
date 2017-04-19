USE `sakuli`;
ALTER TABLE sahi_cases RENAME TO sakuli_cases;
ALTER TABLE sahi_jobs RENAME TO sakuli_jobs;
ALTER TABLE sahi_steps RENAME TO sakuli_steps;
ALTER TABLE sahi_suites RENAME TO sakuli_suites;


ALTER TABLE sakuli_cases CHANGE sahi_suites_id sakuli_suites_id INT(11);
ALTER TABLE sakuli_steps CHANGE sahi_cases_id sakuli_cases_id INT(11);