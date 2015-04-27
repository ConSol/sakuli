

' Simon Meggle, <simon.meggle@consol.de>

' SCRIPT:       showprocs.vbs
' DECRIPTION:   Shows all processes with their full path
' USAGE: 		cscript.exe showprocs.vbs

Dim process, strComputer, objWMIService
strComputer = "."
Set objWMIService = GetObject("winmgmts:" & "{impersonationLevel=impersonate}!\\" & strComputer & "\root\cimv2")

Set colProcess = objWMIService.ExecQuery ("Select * from Win32_Process")	 
For Each objProcess in colProcess
	wscript.echo objProcess.Name & ": " & objProcess.CommandLine
Next

