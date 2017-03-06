#!/bin/bash
# have to be added to hold all env vars correctly

main() {
	# If arg 1 is not maven, execute as it is.
	if [[ $1 =~ mvn ]]; then
        $STARTUPDIR/sakuli_startup.sh "$@"
	else
		# execute any other command, init VNC anyway
		$STARTUPDIR/vnc_startup.sh "$@"
	fi
}

if [ $# -gt 0 ]; then
	# pass all parameters
	main "$@"
else
	# no parameters
	# - run the suite defined by $SAKULI_TEST_SUITE, if set
	# or
	# - run the java example case (fallback)
	main mvn clean test -f "${SAKULI_TEST_SUITE:-/opt/maven}"
fi