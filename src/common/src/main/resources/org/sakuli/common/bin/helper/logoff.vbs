REM logoff.vbs
REM Disconnects the RDP session and attaches it to the local console.
REM Simon Meggle <simon.meggle@consol.de>, 2014 

Dim strcmda, strcmdb, strout,session

strcmda = "cmd.exe /c query session | find /I ""%USERNAME%"""

Set WshShell = CreateObject("WScript.Shell")
Set WshShellExec = WshShell.Exec(strcmda)

strout = WshShellExec.StdOut.ReadAll

elem = Split(strout)
session = Mid(elem(0),2)

strcmdb = "%windir%\System32\tscon.exe " & session & " /dest:console"
WshShell.Exec(strcmdb)

