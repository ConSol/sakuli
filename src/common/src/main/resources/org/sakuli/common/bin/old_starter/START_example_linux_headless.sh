#!/bin/sh

SUITE=example_linux
VNC_DISPLAY=1

PORT="590"$VNC_DISPLAY
notify-send -t 10 -u low -i $SAKULI_HOME/scripts/robotic1.png "Sakuli test '$SUITE' starting now on display $VNC_DISPLAY (localhost:$PORT)."
DISPLAY=:$VNC_DISPLAY
vncserver $DISPLAY -depth 24 -geometry 1024x768 &

#internal folders for application logic
INCLUDE_FOLDER=$SAKULI_HOME/_include
SAKULI_JARS=$SAKULI_HOME/bin/lib/*

java -classpath $SAKULI_HOME/bin/sakuli.jar:$SAKULI_JARS org.sakuli.starter.SakuliStarter --run "$SAKULI_HOME/sakuli_test_suites/$SUITE"

killall Xvnc4
