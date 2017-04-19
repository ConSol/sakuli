'  http://www.sakuli.org

' SCRIPT:       sakuli_ctrl.vbs
' DECRIPTION:   This script can place/remove lock files in a suite folder to prevent Sakuli from testing
'               certain suites. Use on Windows only.
' USAGE:        cscript.exe sakuli_ctrl.vbs sakuli|(suitename) DISABLE|ENABLE
Dim usage
usage = VbCrLf & "Usage: cscript.exe sakuli_ctrl.vbs sakuli|(suitename) DISABLE|ENABLE" & VbCrLf & _
				 "11/2013 Simon Meggle, <simon.meggle@consol.de>" & VbCrLf & _
					 "    If arg1 is a suite name, the .DISABLED file is created in the suite's" & VbCrLf & _
					 "        home directory, which disables this particular Sakuli suite." & VbCrLf & _
					 "    If arg1 is 'sakuli', the .DISABLED file is created in the sakuli" & VbCrLf & _
					 "        home directory, which disables Sakuli completely." & VbCrLf & VbCrLf & _
		             "    If arg2 is 'enable', the .DELETE file gets deleted."

Dim FSObject,SAKULI_HOME,SAKULI_SUITES,FLAG_FILE,arg_action,arg_target,target_file
Const ForWriting=2
target_file = ""

'----- CONFIG 
SAKULI_HOME = "C:\sakuli_app\"
SAKULI_SUITES = "_sakuli_test_suites\"
FLAG_FILE = ".DISABLED"
'----- /CONFIG
Set FSObject = CreateObject("Scripting.FileSystemObject")

If (WScript.Arguments.Count <> 2) Then
	die "UNKNOWN: Invalid number of arguments. Exiting... " & usage, 3
End If

arg_target = LCase(WScript.Arguments(0))
arg_action = LCase(WScript.Arguments(1))

If (arg_action <> "disable") AND (arg_action <> "enable") Then
	die "UNKNOWN: Argument 2 must be either 'enable' or 'disable'. Exiting..." & usage, 3
End If

If (arg_target = "sakuli") Then
	target_file = SAKULI_HOME & FLAG_FILE
Else
	dir_Exists_OrDie SAKULI_HOME & SAKULI_SUITES & arg_target & "\"
	target_file = SAKULI_HOME & SAKULI_SUITES & arg_target & "\" & FLAG_FILE
End If


' create File
If (arg_action = "disable") Then
	FSObject.CreateTextFile target_file, True
	file_Exists_OrDie target_file, "Could not create " & target_file & " !"
	quit "OK: File " & target_file & " successfully created."
else
	On error resume next
	' force delete
	FSObject.DeleteFile target_file, True
	file_Removed_OrDie target_file, "Could not delete " & target_file & " !"
	quit "OK: File " & target_file & " successfully deleted."
End If 



' ###############################################################

Function dir_Exists_OrDie(indir)
	Dim ret, dirnew
	If Not FSObject.FolderExists (indir) Then
		die "UNKNOWN: Directory does not exist: " & indir, 3
	End If
End Function


Sub file_Exists_OrDie(infile, InStr)
	If Not file_Exists(infile) Then
		die "UNKNOWN: " & InStr, 3
	End If
End Sub

Sub file_Removed_OrDie(infile, InStr)
	If file_Exists(infile) Then
		die "UNKNOWN: " & InStr, 3
	End If
End Sub

Function file_Exists(infile)
	Dim ret
	If Not FSObject.FileExists(infile) Then
		file_Exists = False
	Else
		file_Exists = True
	End If
End Function

Sub quit(inmsg)
	WScript.echo inmsg
	WScript.quit(0)
End Sub

Sub die(inmsg, instate)
	WScript.echo inmsg
	WScript.quit(instate)
End Sub
