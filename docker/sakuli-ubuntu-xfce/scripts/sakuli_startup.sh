#!/bin/bash

main() {
	# If arg 1 is not one of the four possible Sakuli COMMANDs, execute as it is. 
	if [[ $1 =~ encrypt|help|version ]]; then
		echo "Executing: 'sakuli $@'"
		$SAKULI_HOME/bin/sakuli "$@"
		exit $?
	elif [ "$1" == "run" ]; then
		vnc_init
		echo "Executing: 'sakuli $@'"
		$SAKULI_HOME/bin/sakuli "$@"
		res=$?
		echo "SAKULI_RETURN_VAL: $res"
		# modify $SAKULI_TEST_SUITE permissions to ensure that volume-mounted log files can be deleted afterwards
		chmod -R a+rw $2
		exit $res
	else
		# execute any other command, init VNC anyway
		vnc_init
		exec $1
	fi
}

# start VNC server
vnc_init() {
        /root/scripts/vnc_startup.sh
}

if [ $# -gt 0 ]; then
	main "$@"
else
        echo "No argument given; fallback to run demo suite example_xfce."
	main run /root/sakuli/example_test_suites/example_xfce
fi
