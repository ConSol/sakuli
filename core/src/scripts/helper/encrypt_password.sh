#!/bin/sh
# script for encrypt a password
secret=test
interface=eth0
java -classpath sakuli.jar:../../bin/lib/* org.sakuli.starter.SakuliStarter -encrypt $secret -interface $interface