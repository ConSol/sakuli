#!/bin/bash

/root/scripts/vnc_startup.sh

# modify $SAKULI_TEST_SUITE permissions to ensure, that volume-mounted log files can be deleted afterwards
chmod -r a+w $SAKULI_TEST_SUITE

for i in "$@"
do
    echo -e "\n----------- resolve script arguments -------------"
    echo -e "\narg_orig: $i"
    i=$(eval echo $i)
    echo "arg_resolved: $i"

    case $i in
        # if option starts with `-` the script arguments are interpreted as arguments for Sakuli
        -*)
        echo -e "\n------------------ START SAKULI ------------------"
        $SAKULI_HOME/bin/sakuli.sh $i
        ;;

        *)
        # unknown option ==> call command
        echo -e "\n----------------- EXECUTE COMMAND ----------------"
        echo -e "\ncmd: $i"
        exec $i
        ;;
    esac
done

res=$?
echo "SAKULI_RETURN_VAL: $res"
exit $res
