#!/bin/bash
VNC_RESOLUTION="1024x768"
VNC_COL_DEPTH=24
VNC_DISPLAY=1

INCLUDE_FOLDER=$SAKULI_HOME/_include
SAKULI_JARS=$SAKULI_HOME/libs/java
ROBOTIC_ICON=$SAKULI_HOME/bin/resources/robotic1.png

options=$@
arguments=($options)
index=0

die_usage() {
	cat <<USAGE
Generic Sakuli test starter.
2015 - The Sakuli team.
 Usage: $0 [options]
USAGE
	sakuli_usage
	cat <<HERE
 -v,--vnc                           start in headless (xVNC11) mode
 -d,--display                       display number in headless mode (default: 1)
 -j,--javahome                      Java bin dir (overrides PATH)
 -Dany.property.key=value           JVM option to set a property on runtime
HERE
	exit 1
}

sakuli_usage() {
	${JAVAHOME}java -classpath $SAKULI_JARS/sakuli.jar:$SAKULI_JARS/* org.sakuli.starter.SakuliStarter -help | grep -v "usage: sakuli"
}

exec_test() {
	${JAVAHOME}java $1 -classpath $SAKULI_JARS/sakuli.jar:$SAKULI_JARS/* org.sakuli.starter.SakuliStarter $2
	return $?
}

vnc_kill() {
	[[ ! $HEADLESS ]] && return
	vncserver -kill $DISPLAY
}

notify() {
	if [[ -x $(which notify-send) ]]; then 
		notify-send -t 10 -u low -i "$ROBOTIC_ICON" "$1"
	fi
}

init_vnc() {
	[[ ! $HEADLESS ]] && return
	if [[ ! -x $(which vncserver) ]]; then
		echo "Cannot start in headless mode: can't locate vncserver."
		exit 1
	fi
	PORT="590"$VNC_DISPLAY
	notify "Sakuli test '$SUITE' starting now on display $VNC_DISPLAY (localhost:$PORT)."
	DISPLAY=:$VNC_DISPLAY
	vncserver $DISPLAY -depth $VNC_COL_DEPTH -geometry $VNC_RESOLUTION &
}

let lastindex=${#arguments[@]}
sakuli_args=""
jvm_args=""
i=0
while [ $i -le $lastindex ]; do
	case ${arguments[$i]} in
		"-h")
			die_usage
			;;
		"--help")
			die_usage
			;;
		"-?")
			die_usage
			;;
		"-v" | "--vnc" )
			HEADLESS=true
			let "i=$i+1"
			;;
		"-d" | "--display" )
			let "j=$i+1"
			VNC_DISPLAY=${arguments[$j]}
			let "i=$i+2"		
			;;
		"-j" | "--javahome" )
			let "j=$i+1"
			JAVAHOME=$(echo ${arguments[$j]} | sed 's/\/$//')/ 
			if [ ! -x "${JAVAHOME}/java" ]; then
				echo "${JAVAHOME}/java is not executable!"
				sakuli_usage	
				exit 1
			fi
			let "i=$i+2"		
			;;
		*)
			# -Dsakuli.property.key=value
			argi=${arguments[$i]}	
			if [ "${argi:1:1}" == "D" ]; then
				jvm_args="$jvm_args $argi"
				let "i=$i+1"		
			else  
				let "j=$i+1"		
				argj=${arguments[$j]}	
				sakuli_args="$sakuli_args $argi $argj"
				let "i=$i+2"		
			fi
			;;
	esac
done

init_vnc
exec_test "$jvm_args" "$sakuli_args"
res=$?
#echo "EXIT_STATE: $res"
vnc_kill
exit $res

