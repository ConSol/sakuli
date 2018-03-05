#!/bin/bash
# have to be added to hold all env vars correctly

main() {
    export SAKULI_UI_DOCKER_USER_ID=$(id -u)
	# If arg 1 is not for sakuli ui jar, execute as it is.
	if [[ "$1" == /* ]] || [[ "$1" == -* ]]; then
        $STARTUPDIR/sakuli_startup.sh "$@"
	else
		# execute any other command
		echo -e "\n\n------------------ EXECUTE COMMAND ------------------"
        echo "Executing command: '$@'"
        exec "$@"
	fi
}

if [ $# -gt 0 ]; then
	# pass all parameters
	main "$@"
else
	# no parameters
	# - run the Sakuli UI at $SAKULI_UI_ROOT, if set
	# or
	# - run at /opt/sakuli-ui(fallback)
	main "${SAKULI_UI_ROOT-/opt/sakuli-ui-root}"
fi