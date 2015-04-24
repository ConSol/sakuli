REM LOGOFF.bat
REM Wrapper Script for logoff.vbs
REM Simon Meggle <simon.meggle@consol.de>, 2014
REM Set a user environent variable %SAKULI_HOME% to the akuli root folder. 
REM Then place this file on the Desktop. 
REM Execute with admin privileges to safely disconnect the current RDP session
REM and attach it to the local console. See logoff.vbs for more details. 

echo off
cscript.exe /NoLogo %SAKULI_HOME%\scripts\helper\vb_scripts\logoff.vbs


