# script for modify the user permissions for the sakuli installation dir
# if no arguments are assigned, will fail

###### parsing arguments
param([string]$pathValue = $null)
#e. g. $path = "C:\sakuli\"

if (! $pathValue){
	Write-Error "first argument have to be the path to set the user permissions"
	exit
}
set-strictmode -version Latest

###### functions & parameters:
function setFullControl ($pathTo){
	$username = $Env:USERNAME
	$acl = Get-Acl "$pathTo"
	$ar = New-Object  system.security.accesscontrol.filesystemaccessrule($username,"FullControl","Allow")
	$acl.SetAccessRule($ar)
	Write-Debug "allow full control for user '$username' on file '$pathTo'"
	Set-Acl "$pathTo" $acl -ErrorAction Stop
}

#set root
setFullControl($pathValue)
#set children
Get-ChildItem "$pathValue" -recurse -Force |% {
	setFullControl($_.fullname)	
}

echo "Folder Permissions of '$pathValue' update sucessfully for user '$Env:USERNAME'!"
exit