#!/bin/bash
# 
# while true; do ./fake_sakuli.sh; sleep 10; done


now=`date +%s`
commandfile='/omd/sites/fo/tmp/run/nagios.cmd'

PERL="perl -le"

#        0       => 0, # OK
#        1       => 1, # WARNING_IN_STEP
#        2       => 1, # WARNING_IN_CASE
#        3       => 1, # WARNING_IN_SUITE
#        4       => 2, # CRITICAL_IN_CASE
#        5       => 2, # CRITICAL_IN_SUITE
#        6       => 2, # EXCEPTION

STATE=0

NOW=$(date +"%d.%m. %H:%M:%S")

random() {
        base=$1
        randomize=$2
        digits=$3
        val=`$PERL "printf \"%0.${digits}f\", $base + rand($randomize)"`
        echo $val
}

case $STATE in
        0)
                suite__state=0
                suite__warning=25
                suite__critical=28
                suite_example_ubuntu=$(random 20 4 2)
                c_001__state=0
                c_001__warning=15
                c_001__critical=18
                c_001_case1=$(random 16 3 2)
                s_001_001_Test_Sahi_landing_page=$(random 3 3 2)
                s_001_001_Test_Sahi_landing_page_warning=10
                s_001_002_Calculation=$(random 4 3 2)
                s_001_002_Calculation_warning=9
                s_001_003_Editor=$(random 2 2 2)
                s_001_003_Editor_warning=7

                example_ubuntu="sakuli_client;example_ubuntu;$STATE;OK - OK - [OK] Sakuli suite 'example_ubuntu' ran in $suite_example_ubuntu seconds - ok. (Last suite run: $NOW)|suite__state=$suite__state;;;; suite__warning=${suite__warning}s;;;; suite__critical=${suite__critical}s;;;; suite_example_ubuntu=${suite_example_ubuntu}s;$suite__warning;$suite__critical;; c_001__state=$c_001__state;;;; c_001__warning=${c_001__warning}s;;;; c_001__critical=${c_001__critical}s;;;; c_001_case1=${c_001_case1}s;$c_001__warning;$c_001__critical;; s_001_001_Test_Sahi_landing_page=${s_001_001_Test_Sahi_landing_page}s;$s_001_001_Test_Sahi_landing_page_warning;;; s_001_002_Calculation=${s_001_002_Calculation}s;$s_001_002_Calculation_warning;;; s_001_003_Editor=${s_001_003_Editor}s;$s_001_003_Editor_warning;;; [check_sakuli]"
                example_ubuntu_db="sakuli_client;example_ubuntu_db;$STATE;OK - [OK] case 'case1' ran in ${c_001_case1}s - ok, [OK] Sakuli suite 'example_ubuntu' (ID: 1205) ran in $suite_example_ubuntu seconds. (Last suite run: $NOW)|suite_example_ubuntu=${suite_example_ubuntu}s;$suite__warning;$suite__critical;; s_001_001_Test_Sahi_landing_page=${s_001_001_Test_Sahi_landing_page}s;$s_001_001_Test_Sahi_landing_page_warning;;; s_001_002_Calculation=${s_001_002_Calculation}s;$s_001_002_Calculation_warning;;; s_001_003_Editor=${s_001_003_Editor}s;$s_001_003_Editor_warning;;; c_001_case1=${c_001_case1}s;$c_001__warning;$c_001__critical;; c_001__state=$c_001__state;;;; c_001__warning=${c_001__warning}s;;;; c_001__critical=${c_001__critical}s;;;; suite__state=$suite__state;;;; suite__warning=${suite__warning}s;;;; suite__critical=${suite__critical}s;;;;"

                ;;
        1)
                ;;
        2)
                ;;
        3)
                ;;
        4)
                ;;
        5)
                ;;
        6)
                ;;

esac

printf "[%lu] PROCESS_SERVICE_CHECK_RESULT;$example_ubuntu\n" $now > $commandfile
printf "[%lu] PROCESS_SERVICE_CHECK_RESULT;$example_ubuntu_db\n" $now > $commandfile
