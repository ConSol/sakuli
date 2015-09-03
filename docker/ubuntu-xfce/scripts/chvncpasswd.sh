#!/usr/bin/expect
#get env variable $VNC_PW and set new vncpasswd

spawn vncpasswd
expect "Password:"
send "$env(VNC_PW)\r"
expect "Verify:"
send "$env(VNC_PW)\r"

interact
