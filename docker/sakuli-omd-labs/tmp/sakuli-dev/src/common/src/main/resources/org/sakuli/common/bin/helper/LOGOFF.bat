REM LOGOFF.bat
REM Wrapper Script for logoff.vbs
REM Run this script with admin privileges from a Windows Desktop to automatically 
REM disconnect the current RDP session and redirect it to the local console.
REM SAKULI_HOME must be set as a system variable.
REM Simon Meggle <simon.meggle@consol.de>, 2014
REM See https://github.com/ConSol/sakuli/blob/master/docs/additional-settings.md
echo off
cscript.exe /NoLogo %SAKULI_HOME%\scripts\helper\logoff.vbs
