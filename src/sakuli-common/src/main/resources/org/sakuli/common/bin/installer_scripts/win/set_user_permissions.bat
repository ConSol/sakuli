@echo off
SET basedir=%~dp0
set param=%~1
echo 'EXECUTE: powershell -executionPolicy bypass -file "%basedir%\ps\modfiy_user_permissions.ps1" "%param%"'

REM echo | is neccessary to not block the execution if the cmd script is called from java
echo | powershell.exe -executionPolicy bypass -file "%basedir%\ps\modfiy_user_permissions.ps1" "%param%"

echo 'END!'
