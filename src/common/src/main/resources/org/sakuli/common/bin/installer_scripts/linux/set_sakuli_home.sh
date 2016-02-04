#!/bin/bash

function remove_sakuli_home {
    echo "remove 'SAKULI_HOME' from 'bash.rc'"
    unset SAKULI_HOME
    [ -r ~/.bashrc ] && sed -i '/export SAKULI_HOME/d' ~/.bashrc
}

function set_sakuli_home {
    if [ -d "$1" ]; then
        echo "set 'SAKULI_HOME' in 'bash.rc' to: $1"
        echo "export SAKULI_HOME=$1" >> ~/.bashrc
        export SAKULI_HOME=$1
    else
        echo "new 'SAKULI_HOME' path '$1' does not exists!"
        exit 1
    fi
}

function set_path {
    echo "$PATH"
    if grep -q SAKULI_HOME/bin: ~/.bashrc 2>/dev/null; then
        echo "no update of 'PATH' needed!"
    else
        echo "add 'SAKULI_HOME/bin' to PATH"
        echo -e "export PATH=\$SAKULI_HOME/bin:\$PATH" >> ~/.bashrc
        export PATH=$SAKULI_HOME/bin:$PATH
    fi
}

function remove_path {
    if grep -q SAKULI_HOME/bin: ~/.bashrc 2>/dev/null; then
        echo "remove 'SAKULI_HOME' from PATH"
        [ -r ~/.bashrc ] && sed -i '/SAKULI_HOME\/bin/d' ~/.bashrc
        PATH=${PATH/":$SAKULI_HOME"/} # delete any instances in the middle or at the end
        PATH=${PATH/"$SAKULI_HOME:"/} # delete any instances at the beginning
    else
      echo "no update of 'PATH' needed!"
    fi
}

remove_sakuli_home
remove_path
if [ ! "${1:0:1}" == "" ]; then
    sakuli_inst_path=$(eval echo $*)
    set_sakuli_home $sakuli_inst_path
    set_path
fi
