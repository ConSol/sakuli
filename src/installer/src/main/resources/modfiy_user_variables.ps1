param([string]$newSakuliHome = "")
#$newSakuliHome = "C:\DATEN\SA22\sakuli-v0.9.2-SNAPSHOT"
Write-Host "SET user environment variable 'SAKULI_HOME': $newSakuliHome"
Set-ItemProperty -Path 'Registry::HKEY_CURRENT_USER\Environment' -Name SAKULI_HOME -Value $newSakuliHome