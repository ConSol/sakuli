@echo off
echo Generic Sakuli test starter.
echo 2015 - The Sakuli team
echo http://www.sakuli.org
echo http://github.com/ConSol/sakuli
echo

setLocal EnableDelayedExpansion
 
set SAKULI_JARS=%SAKULI_HOME%\libs\java
set JAVAHOME=
set SAKULI_PARAMS=
set JAVA_PARAMS=
set count=0
 
:nextParam
  set param=%1
  if "%param%" == "" goto execute
  set /A count+=1
  IF "%param%" == "-j" (
        SET JAVAHOME=%~2
        shift
  ) ELSE IF "%param%" == "--javahome" (
        SET JAVAHOME=%~2
        shift
  ) ELSE IF "%param:~0,2%" == "-D" (
        set JAVA_PARAMS=%JAVA_PARAMS% "%param%=%2"
        shift
  ) ELSE (
        set SAKULI_PARAMS=%SAKULI_PARAMS% %param%
  )
  shift
goto nextParam
 
:execute
echo executed command: "!JAVAHOME!java" %JAVA_PARAMS% -classpath %SAKULI_JARS%\sakuli.jar;%SAKULI_JARS%\* org.sakuli.starter.SakuliStarter %SAKULI_PARAMS%%
"!JAVAHOME!java" %JAVA_PARAMS% -classpath %SAKULI_JARS%\sakuli.jar;%SAKULI_JARS%\* org.sakuli.starter.SakuliStarter %SAKULI_PARAMS%%
IF NOT %ERRORLEVEL% == 100 goto end
 
::print usage if rc was 100 (=something went wrong, sakuli.jar showed up the usage)
echo  -j,--javahome                      Java bin dir (overrides PATH)
echo  -Dany.property.key=value           JVM option to set a property on runtime

:end
