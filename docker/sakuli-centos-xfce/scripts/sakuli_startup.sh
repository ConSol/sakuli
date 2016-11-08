#!/bin/bash

main() {
	# If arg 1 is not one of the four possible Sakuli COMMANDs, execute as it is.
	if [[ $1 =~ encrypt|help|version ]]; then
		echo "Executing: 'sakuli $@'"
		$SAKULI_HOME/bin/sakuli "$@"
		exit $?
	elif [ "$1" == "run" ]; then
		# Important for folder permissions to ensure that volume-mounted log
		# files can be deleted afterwards
		umask ${SAKULI_UMASK:-0000}
		vnc_init
		echo "Executing: 'sakuli $@'"
		$SAKULI_HOME/bin/sakuli "$@"
		res=$?
		echo "SAKULI_RETURN_VAL: $res"
		exit $res
	else
		# execute any other command, init VNC anyway
		vnc_init
		exec $1
	fi
}

# start VNC server
vnc_init() {
	# Includes /headless/scripts/generate_container_user
	/headless/scripts/vnc_startup.sh
}

if [ $# -gt 0 ]; then
	# pass all parameters
	main "$@"
else
	# no parameters
	# - run the suite defined by $SAKULI_TEST_SUITE, if set
	# or
	# - run the example_xfce case (fallback)
	main run ${SAKULI_TEST_SUITE:-/sakuli/example_test_suites/example_xfce}
fi
