<#
Downloads FirefoxPortable for windows and installs it
#>
###### parsing arguments
param([string]$pathValue = $null)
#e. g. $path = "C:\sakuli\"

if (! $pathValue){
	Write-Error "first argument have to be the path to install Firefox portable"
	exit
}
set-strictmode -version Latest

$firefox_zip = "FirefoxPortable-v42-windows.zip"
$url = "https://labs.consol.de/sakuli/install/3rd-party/ff_portable/" + $firefox_zip
$storageDir = "$Env:temp"
$file = "$storageDir\" + $firefox_zip


New-Item -ItemType Directory -Force -Path $pathValue
$webclient = New-Object System.Net.WebClient
echo "Download: $url"
$webclient.DownloadFile($url,$file)
$shell_app=new-object -com shell.application
$zip_file = $shell_app.namespace($file)
echo "Unzip to '$pathValue'"
$destination = $shell_app.namespace($pathValue)
$destination.Copyhere($zip_file.items())
echo "delete tmp file $file"
Remove-Item $file