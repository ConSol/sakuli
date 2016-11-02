#!/bin/sh

SAKULI_PROJECT_HOME=~/git-files/sakuli
SAKULI_INST_FOLDER=$SAKULI_PROJECT_HOME/sakuli_app
SAKULI_VERSION=0.9.2-SNAPSHOT
export SAKULI_HOME=$SAKULI_INST_FOLDER/sakuli/sakuli-v$SAKULI_VERSION

SUITE=example_test_suites/example_ubuntu

VNC_DISPLAY=1

PORT="590"$VNC_DISPLAY
notify-send -t 10 -u low -i $SAKULI_HOME/bin/resources/robotic1.png "Sakuli test '$SUITE' starting now on display $VNC_DISPLAY (localhost:$PORT)."
export DISPLAY=:$VNC_DISPLAY

#vncserver should run!
#vncserver $DISPLAY -depth 24 -geometry 1024x768 &

$SAKULI_INST_FOLDER/sakuli/sakuli-v$SAKULI_VERSION/bin/sakuli run "$SAKULI_PROJECT_HOME/$SUITE"

#killall Xvnc4
