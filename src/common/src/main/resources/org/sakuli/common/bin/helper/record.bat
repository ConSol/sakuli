set REC=%ProgramFiles%\CamStudio 2.7\CamCommandLine.exe
set hour=%TIME:~0,2%
set min=%TIME:~3,2%
set FILE="c:\mov\%1_%hour%%min%.avi"
"%REC%" -codec 3 -outfile %FILE% -seconds %2 -fps 8
exit



