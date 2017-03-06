#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install example apps for sakuli testcase 'https://github.com/ConSol/sakuli/tree/master/example_test_suites/example_xfce'"
apt-get update 
apt-get install -y --fix-missing gedit gnome-calculator
apt-get clean -y