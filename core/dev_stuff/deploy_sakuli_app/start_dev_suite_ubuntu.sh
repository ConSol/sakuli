#!/bin/sh

SAKULI_PROJECT_HOME=~/git-files/sakuli
SAKULI_INST_FOLDER=$SAKULI_PROJECT_HOME/sakuli_app
SAKULI_VERSION=0.4.9-SNAPSHOT
SAKULI_HOME=$SAKULI_INST_FOLDER/sakuli
SAHI_HOME=$SAKULI_PROJECT_HOME/sahi

SUITE=core/_dev_test_suites/_testsuite1_Ubuntu_BB

VNC_DISPLAY=1

PORT="590"$VNC_DISPLAY
notify-send -t 10 -u low -i $SAKULI_HOME/scripts/robotic1.png "Sakuli test '$SUITE' starting now on display $VNC_DISPLAY (localhost:$PORT)."
DISPLAY=:$VNC_DISPLAY

#vncserver should run!
#vncserver $DISPLAY -depth 24 -geometry 1024x768 &

#internal folders for application logic
INCLUDE_FOLDER=$SAKULI_HOME/_include
SAKULI_JARS=$SAKULI_HOME/bin/lib/*

java -classpath $SAKULI_HOME/bin/sakuli.jar:$SAKULI_JARS org.sakuli.starter.SakuliStarter -run "$SAKULI_PROJECT_HOME/$SUITE" "$INCLUDE_FOLDER" "$SAHI_HOME"

#killall Xvnc4
