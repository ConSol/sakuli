
'* Copyright (C) 2012  Simon Meggle, <simon.meggle@consol.de>
'* 
'* 
'* this program Is free software; you can redistribute it And/Or
'* modify it under the terms of the GNU General Public License
'* As published by the Free Software Foundation; either version 2
'* of the License, Or (at your Option) any later version.
'* 
'* this program Is distributed In the hope that it will be useful,
'* but WITHOUT ANY WARRANTY; without even the implied warranty of
'* MERCHANTABILITY Or FITNESS For A PARTICULAR PURPOSE.  See the
'* GNU General Public License For more details.
'* 
'* You should have received a copy of the GNU General Public License
'* along With this program; If Not, write To the Free Software
'* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
'
' SCRIPT:       modwindow.vbs
' DECRIPTION:   Brings a certain to foreground/background
' USAGE: 		cscript.exe modwindow.vbs maximize "Exact window title"

Dim resm, title, oShell

title = WScript.Arguments(0)
action = WScript.Arguments(1)

Set oShell = CreateObject("WScript.Shell")
res = oShell.AppActivate(title)

If res = True Then
	Select Case action 
		Case "maximize"
			'oShell.SendKeys("{F11}")
			oShell.SendKeys("% ")
			oShell.SendKeys("x")
		Case "minimize"
			oShell.SendKeys("% ")
			oShell.SendKeys("n")
		Case "close"
			oShell.SendKeys("%{F4}")
	End Select	
	'WScript.Sleep 100
End If

Set oShell = Nothing