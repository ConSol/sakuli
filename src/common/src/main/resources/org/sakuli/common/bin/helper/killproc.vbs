' Simon Meggle, <simon.meggle@consol.de>

' SCRIPT:       killproc.vbs
' DECRIPTION:   Kills all occurrences of a process, given by the FULL command line (as displayed in task manager)
' USAGE: 		cscript.exe killproc.vbs process.exe
'				cscript.exe killproc.vbs -f procs_to_kill.txt

Dim process, strComputer, fso, procfile, objShell, file_procstokill

' default
file_procstokill = "C:\sakuli\scripts\helper\vb_scripts\procs_to_kill.txt"

strComputer = "."
Set objWMIService = GetObject("winmgmts:" _
	& "{impersonationLevel=impersonate}!\\" _
	& strComputer & "\root\cimv2")

WScript.echo "--------------------------------"
if WScript.Arguments(0) = "-f" Then
	file_procstokill = WScript.Arguments(1)
	
	Wscript.echo "Killing all processes with path pattern named in " & file_procstokill & "..."
	set fso = CreateObject("Scripting.FileSystemObject")
	set procfile = fso.OpenTextFile(file_procstokill)
	Dim continue
	do while not procfile.AtEndOfStream
		continue = True
		process = procfile.ReadLine()
		if (Mid(process,1,1) = "#") or (process = "") Then continue = false end if
		if continue then
			WScript.Echo "Going to kill with pattern " & process & "..."
			s_kill(process)
		end if
	loop
	
Else
	process = WScript.Arguments(0)
	Wscript.echo "Killing all processes with path pattern " & process & "..."
	s_kill(process)
End If

Wscript.echo "Sleeping 3s ..."
Wscript.sleep(3000)
WScript.echo "...done."
WScript.echo "--------------------------------"



sub s_kill(p)
	Set colProcess = objWMIService.ExecQuery _
		 ("Select * from Win32_Process Where CommandLine LIKE '%" & Replace(process, "\", "\\") & "%'")
	'Set colProcess = objWMIService.ExecQuery _
	'	 ("Select * from Win32_Process Where CommandLine LIKE '%phantomjs.exe%'")
		 
	For Each objProcess in colProcess
			Wscript.echo "Killed: [" & objProcess.ProcessID & "] " & objProcess.Name
			objProcess.Terminate
	Next
end sub
