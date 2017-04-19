#!/bin/bash

function remove_var {
    echo "remove '$1' from 'bash.rc'"
    unset $1
    regex="/export $1/d"
#    echo "regex to remove: $regex"
    [ -r ~/.bashrc ] && sed -i "$regex" ~/.bashrc
}

function set_var {
    echo "set '$1' in 'bash.rc' to: 1"
    echo "export $1='1'" >> ~/.bashrc
    export $1='1'
}

FF_VARS=("MOZ_DISABLE_OOP_PLUGINS" "MOZ_DISABLE_AUTO_SAFE_MODE" "MOZ_DISABLE_SAFE_MODE_KEY")

for ((i=0; i < ${#FF_VARS[@]}; i++))
do
  cur_var=${FF_VARS[$i]}
  echo $cur_var
  remove_var $cur_var
  if [ "${1:0:3}" == "set" ]; then
    set_var $cur_var
  fi
done

