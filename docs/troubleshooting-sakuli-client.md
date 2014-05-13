

# Troubleshooting Sakuli-Client
## Sahi
### Sahi does not start
_**When I click on the "Start Sahi" icon on the desktop to Start Sahi Dashboard, nothing comes up.**_

#### Check PATH
Open _**%SAKULI_HOME%**\sahi\userdata\bin\start_dashboard.bat_ insert a new line on the end of the script and add "pause". Save te file and try to start Sahi again. If the error message is like *"command 'java' was not found"*, you should check if _**%PATH%**_ is containing the right path to the java executable.   

### No browsers in Dashboard
_**When I open the Sahi dashboard, no browsers are shown.**_
![nobrowser](../docs/pics/w_sahi_no_browser.jpg) 

Open _**SAKULI_HOME**\sahi\userdata\config\browser_types.xml_. Each browser is defined within a **browserType** block. #### Check variable ProgramFiles
"path" probably contains the variable **"$ProgramFiles (x86)…"**, which is wrong. *$ProgramFiles* itself resolves already to the right folder, hence you should delete the " (x86)" part. Restart the Sahi dashboard and try again.	


## Sakuli
### cannot resolve mac address

FIXME

![macadress](../docs/pics/w_macaddress.jpg) 
### Suite does not start
#### Sikuli lib path


FIXME