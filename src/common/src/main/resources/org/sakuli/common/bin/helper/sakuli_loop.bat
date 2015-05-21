:LOOP
start /wait cmd /c %SAKULI_HOME%\bin\sakuli.bat --run %SAKULI_HOME%\..\example_test_suites\test1\
ping localhost -n 30
start /wait cmd /c %SAKULI_HOME%\bin\sakuli.bat --run %SAKULI_HOME%\..\example_test_suites\test2\
ping localhost -n 30
start /wait cmd /c %SAKULI_HOME%\bin\sakuli.bat --run %SAKULI_HOME%\..\example_test_suites\test2\
ping localhost -n 30
GOTO LOOP
