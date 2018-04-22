REM Use this code in front of Sakuli checks (e.g. as a pre hook) to check if there are any updates in the remote repository, or any local changes which first have to be committed. 

@echo off
cmd /c ""c:\Program Files\Git\bin\bash.exe" --login -i -- c:\Users\sakuli\testsuites.git\starter\git_status.sh c:\users\sakuli\testsuites.git"

REM 0 - Clean
REM 1 - Untracked files
REM 2 - Need to pull
REM 3 - Need to push 

IF ERRORLEVEL 3 (
    ECHO Local repository contains modifications. Manual push needed!
    PAUSE
    EXIT 3
) ELSE IF ERRORLEVEL 2 ( 
    ECHO Fetching updates from remote repository... 
    git pull
    IF ERRORLEVEL 0 (
        ECHO ...OK.
        PAUSE
        EXIT 0
    ) ELSE (
        ECHO ...FAILED. 
        PAUSE
        EXIT 255
    )
) ELSE IF ERRORLEVEL 1 ( 
    ECHO Working directory is not clean. Please commmit. 
    PAUSE
    EXIT 1
) ELSE IF ERRORLEVEL 0 ( 
    ECHO Local repository is up-to-date. 
    PAUSE
    EXIT 0
) 
