REM LOGOFF.bat
REM Wrapper Script for logoff.vbs
REM Simon Meggle <simon.meggle@consol.de>, 2014
REM Place this file on the Desktop. 
REM Execute with admin privileges to safely disconnect the RDP session
REM and attach it to the local console. See logoff.vbs for more details. 

echo off
cscript.exe /NoLogo C:\sakuli\logoff.vbs


