# script for modify the user variables with SAKULI_HOME
# call the script via cmd `powershell -executionPolicy bypass -file "modfiy_user_variables.ps1" "VALUE"`
# if no arguments are assigned, the script will delete SAKULI_HOME from the PATH

###### parsing arguments

param([string]$newSakuliHome = $null)
#e. g. $newSakuliHome = "C:\sakuli\sakuli-v0.9.2-SNAPSHOT"

set-strictmode -version Latest

###### functions & parameters:

$key = "SAKULI_HOME"
$sakuliPathValue = "$newSakuliHome\bin"

function setUserVarSakuliHome([string]$newValue){
    echo "SET user environment variable $key: $newValue"
    Set-ItemProperty -Path 'Registry::HKEY_CURRENT_USER\Environment' -Name $key -Value $newValue
}

function setUserVarPath([string]$newValue){
    echo "SET user environment variable PATH: $newValue"
    Set-ItemProperty -Path 'Registry::HKEY_CURRENT_USER\Environment' -Name PATH -Value $newValue
}

function unsetUserVar([string]$keyValue) {
    $present = Get-ItemProperty -Path 'Registry::HKEY_CURRENT_USER\Environment' -Name "$keyValue" -ErrorAction SilentlyContinue
	if ($present){
		echo "CLEAR user environment vairable '$keyValue'"
    	Remove-ItemProperty -Path 'Registry::HKEY_CURRENT_USER\Environment' -Name "$keyValue"
	} else {
		echo "'SAKULI_HOME' is not set as environment variable, so nothing to do!"
	}
}

function getPathValue(){
	$present = Get-ItemProperty -Path 'Registry::HKEY_CURRENT_USER\Environment' -Name PATH -ErrorAction SilentlyContinue
	if ($present){
		return $present.Path
	}
	return $null
}

function isSemiColumNeeded($pathValue){
	return !(($pathValue -eq $null) -or ($pathValue -ceq "") -or ($pathValue.EndsWith(";")))
}

function addSakuliHomeToPath() {
	$oldPath = getPathValue
	echo "OLD PATH: $oldPath"
	
	if ($oldPath -cmatch '.*sakuli-v.*' ){
		echo "Value does already exist! Remove old value!"
		removeSakuliHomeFromPath
		$oldPath = getPathValue
	}
	echo "Add to user PATH: $sakuliPathValue"
	$newPath = $oldPath;
	if(isSemiColumNeeded($newPath)){
		$newPath += ";"
	}
	$newPath += $sakuliPathValue
	
	#echo $newPath
	setUserVarPath($newPath);
	
	# %SAKULI_HOME% will be resolved
	$curPath =  getPathValue
	echo "New resolved PATH: $curPath"
}

function removeSakuliHomeFromPath() {
	$oldPath = getPathValue	
	if($oldPath){
		$newPath = ""
		$pathArray = $oldPath.split(';')
		foreach ($pathString in $pathArray) {
			if (($pathString -cmatch '.*sakuli-v.*') -or ($pathString -cmatch '.*SAKULI_HOME.*') ){
				echo "Remove from Path: $pathString"
			} else {
				if(isSemiColumNeeded($newPath)){
					$newPath += ";"
				}
				$newPath += $pathString
			}
		}
		if(!($oldPath -ceq $newPath)){
			echo "OLD PATH: $oldPath"
			setUserVarPath($newPath);
			
			# %SAKULI_HOME% should be removed
			$curPath =  getPathValue
			echo "New resolved PATH: $curPath"
			return
		}
	}
	echo "'SAKULI_HOME' is not present on PATH, so nothing to do!"
}

function notifyWindowsEnvironmentChange(){
	echo "Notify Windows about Environment Change!"

Add-Type -TypeDefinition @"
    using System;
    using System.Runtime.InteropServices;

    public class NativeMethods
    {
        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern IntPtr SendMessageTimeout(
            IntPtr hWnd, uint Msg, UIntPtr wParam, string lParam,
            uint fuFlags, uint uTimeout, out UIntPtr lpdwResult);
    }
"@

$HWND_BROADCAST = [IntPtr] 0xffff
$WM_SETTINGCHANGE = 0x1a
$SMTO_ABORTIFHUNG = 0x2
$result = [UIntPtr]::Zero

[void] ([Nativemethods]::SendMessageTimeout($HWND_BROADCAST, $WM_SETTINGCHANGE, [UIntPtr]::Zero, 'Environment', $SMTO_ABORTIFHUNG, 5000, [ref] $result))
}

###### excecution logic
	
if ($newSakuliHome) {
	setUserVarSakuliHome($newSakuliHome)
	addSakuliHomeToPath
} else {
    unsetUserVar($key)
	removeSakuliHomeFromPath
}
notifyWindowsEnvironmentChange
echo "Environment update FINISHED!"
exit