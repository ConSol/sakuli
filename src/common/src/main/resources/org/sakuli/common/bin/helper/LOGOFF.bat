REM LOGOFF.bat
REM Wrapper Script for logoff.vbs
REM Simon Meggle <simon.meggle@consol.de>, 2014
REM See https://github.com/ConSol/sakuli/blob/master/docs/additional-settings.md
echo off
cscript.exe /NoLogo %SAKULI_HOME%\scripts\helper\logoff.vbs
