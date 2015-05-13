#!/bin/bash

# Automatic cleanup for Sakuli result tables

mysql sakuli<<EOFMYSQL
DELETE FROM sakuli_suites where time < DATE_SUB(NOW(), INTERVAL 3 DAY);
DELETE FROM sakuli_cases where time < DATE_SUB(NOW(), INTERVAL 3 DAY);
DELETE FROM sakuli_steps where time < DATE_SUB(NOW(), INTERVAL 3 DAY);
EOFMYSQL

echo "FLUSH QUERY CACHE;" | mysql
for db in sakuli
do
    TABLES=$(echo "USE $db; SHOW TABLES;" | mysql | grep -v 'Tables_in')
    echo "Switching to database $db."
    for table in $TABLES
    do
        echo -n "Optimizing table $table... "
        echo "USE $db; OPTIMIZE TABLE $table" |mysql
        echo "done."
    done
done

