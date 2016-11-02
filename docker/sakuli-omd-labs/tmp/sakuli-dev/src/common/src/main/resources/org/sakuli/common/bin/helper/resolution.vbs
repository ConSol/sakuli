on error resume next
Dim logfile
Const ForAppending=8
Const ForWriting=2
Set fso = CreateObject("Scripting.FileSystemObject")

backuppath = "C:\sakuli_app\tmp\"

If Not fso.FileExists(backuppath & "Resolution.txt") Then
		 fso.CreateTextFile(backuppath & "Resolution.txt")
End If

set logfile = fso.OpenTextFile(backuppath & "Resolution.txt", ForWriting, True)



Set wmiObjSet2 = GetObject("winmgmts:{impersonationLevel=impersonate}"). _
		 InstancesOf("Win32_DesktopMonitor")

logfile.writeline "------------------"
logfile.writeline "Please verify the"
logfile.writeline "screen resolution."
logfile.WriteBlankLines(1)
logfile.WriteLine now()

For Each obj2 In wmiObjSet2
		 logfile.WriteLine "Device ID: " & obj2.DeviceID
		 logfile.WriteLine "Description: " & obj2.Description
		 logfile.WriteLine "Horizontal Resolution: " & obj2.PixelsPerXLogicalInch
		 logfile.WriteLine "Vertical Resolution: " & obj2.PixelsPerYLogicalInch
		 logfile.WriteLine "Screen Width: " & obj2.ScreenWidth
		 logfile.WriteLine "Screen Height: " & obj2.ScreenHeight
		 logfile.WriteBlankLines(1)
Next


logfile.close