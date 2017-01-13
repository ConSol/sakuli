#!/bin/bash
### Startup Script for the Sakuli headless testing container
export _JAVA_OPTIONS=-Duser.home=$HOME

main() {
	# If arg 1 is not one of the four possible Sakuli COMMANDs, execute as it is.
	if [[ $1 =~ run|encrypt|help|version ]]; then
        # Important for folder permissions to ensure that volume-mounted log
		# files can be deleted afterwards
		umask ${SAKULI_UMASK:-0000}

		## start UI and VNC components
		$HOME/scripts/vnc_startup.sh

		echo -e "\n\n------------------ start Sakuli -----------------------------"
		echo "Executing: 'sakuli $@'"
		$SAKULI_HOME/bin/sakuli "$@"
		res=$?
		echo "SAKULI_RETURN_VAL: $res"
		exit $res
	else
		# execute any other command, init VNC anyway
		$HOME/scripts/vnc_startup.sh "$@"
	fi
}

if [ $# -gt 0 ]; then
	# pass all parameters
	main "$@"
else
	# no parameters
	# - run the suite defined by $SAKULI_TEST_SUITE, if set
	# or
	# - run the example_xfce case (fallback)
	main run ${SAKULI_TEST_SUITE:-$SAKULI_ROOT/example_test_suites/example_xfce}
fi