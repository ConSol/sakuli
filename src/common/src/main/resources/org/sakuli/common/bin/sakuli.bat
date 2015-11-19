@echo on

REM set tests=c:\foo bar
REM set java8bin=c:\foo bar\Java\jre1.8.0_40\bin\
REM C:\sakuli\sakuli-v0.9.2-SNAPSHOT\bin\sakuli.bat -j "%java8bin%" --run "%tests%\example_windows"


if "%SAKULI_HOME%" == "" (
  SET SAKULI_HOME=..\
)

set SAKULI_JARS=%SAKULI_HOME%\libs\java
set JAVAEXEC=java
set SAKULI_PARAMS=
set JAVA_PARAMS=
set count=0

:nextParam
  set param=%~1
  if "%param%" == "" goto execute
  set /A count+=1
  IF "%param%" == "-j" (
        SET JAVAEXEC=%~2\java.exe
        shift
  ) ELSE IF "%param%" == "--javahome" (
        SET JAVAEXEC=%~2\java.exe
        shift
  ) ELSE IF "%param:~0,2%" == "-D" (
        set JAVA_PARAMS=%JAVA_PARAMS% "%param%=%2"
        shift
  ) ELSE (
        set SAKULI_PARAMS=%SAKULI_PARAMS% "%param%"
  )
  shift
goto nextParam
 
:execute
"%JAVAEXEC%" %JAVA_PARAMS% -classpath %SAKULI_JARS%\sakuli.jar;%SAKULI_JARS%\* org.sakuli.starter.SakuliStarter %SAKULI_PARAMS%
IF NOT %ERRORLEVEL% == 100 goto end
 
::print usage if rc was 100 (=something went wrong, sakuli.jar showed up the usage)
echo  -j,--javahome                      Java bin dir (overrides PATH)
echo  -Dany.property.key=value           JVM option to set a property on runtime
echo. 
:end
