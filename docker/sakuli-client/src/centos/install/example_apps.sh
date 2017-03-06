#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install example apps for sakuli testcase 'https://github.com/ConSol/sakuli/tree/master/example_test_suites/example_xfce'"
yum -y install gedit gnome-calculator
yum clean all