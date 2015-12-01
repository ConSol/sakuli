if "%SAKULI_HOME%"=="" (
   echo "Environment variable 'SAKULI_HOME' is not set!" \
          "Please ensure that 'SAKULI_HOME' points to the installation folder of your expected Sakuli version!"
   goto end
)

SET basedir=%~dp0
%SAKULI_HOME%/bin/sakuli.bat --run %basedir%\example_windows
:end
