#!/usr/bin/env bash
# Return reasonable JVM options to run inside a Docker container where memory and
# CPU can be limited with cgroups.
# https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/parallel.html
#
# The script can be used in a custom CMD or ENTRYPOINT
#
# source jvm_options

# Options:
#   JVM_HEAP_RATIO=0.25 Ratio of heap size to available memory

# If Xmx is not set the JVM will use by default 1/4th (in most cases) of the host memory
# This can cause the Kernel to kill the container if the JVM memory grows over the cgroups limit
# because the JVM is not aware of that limit and doesn't invoke the GC
# Setting it by default to 0.25 times the memory limited by cgroups, customizable with JVM_HEAP_RATIO

if [ -z "$JVM_HEAP_XMX" ] ; then
    CGROUPS_MEM=$(cat /sys/fs/cgroup/memory/memory.limit_in_bytes)
    echo CGROUPS_MEM: $CGROUPS_MEM bytes, $(($CGROUPS_MEM/1024/1024)) MB
    MEMINFO_MEM=$(($(awk '/MemTotal/ {print $2}' /proc/meminfo)*1024))
    echo MEMINFO_MEM: $MEMINFO_MEM bytes, $(($MEMINFO_MEM/1024/1024)) MB
    MEM=$(($MEMINFO_MEM>$CGROUPS_MEM?$CGROUPS_MEM:$MEMINFO_MEM))
    echo MEM: $MEM bytes, $(($MEM/1024/1024)) MB

    JVM_HEAP_RATIO=${JVM_HEAP_RATIO:-0.25}
    echo "JVM_HEAP_RATIO in % $JVM_HEAP_RATIO"
    JVM_HEAP_XMX=$(awk '{printf("%d",$1*$2/1024^2)}' <<<" ${MEM} ${JVM_HEAP_RATIO} ")

    JVM_HEAP_XMX_MAX=${JVM_HEAP_XMX_MAX:-1024}
    if [ $JVM_HEAP_XMX -gt $JVM_HEAP_XMX_MAX ]; then
        echo "calculated JVM_HEAP_XMX $JVM_HEAP_XMX MB is too high, choose JVM_HEAP_XMX_MAX: $JVM_HEAP_XMX_MAX MB"
        JVM_HEAP_XMX=$JVM_HEAP_XMX_MAX
    fi
    
fi
echo "JVM_HEAP_XMX: $JVM_HEAP_XMX MB"

# set correct java startup
export _JAVA_OPTIONS="-Duser.home=$HOME -Xmx${JVM_HEAP_XMX}m"
echo "set _JAVA_OPTIONS: $_JAVA_OPTIONS"
