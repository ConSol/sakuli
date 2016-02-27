REM Delete sqlite files from profile directory before each test run. 
REM See https://bugzilla.mozilla.org/show_bug.cgi?id=686237
REM and https://bugzilla.mozilla.org/show_bug.cgi?id=857888
set datadir="%SAKULI_HOME%\..\sahi\userdata\browser\ff\profiles\sahi0"
del /Q /F %datadir%\*.sqlite*
