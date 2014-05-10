

# Troubleshooting Sakuli-Client
## Sahi
### No browsers in Dashboard
Chances are that Sahi cannot find any useable browser: ![nobrowser](../docs/pics/w_sahi_no_browser.jpg) 	
Solution: 

* click on *configure*
* On each *<browserType>* section, replace *$ProgramFiles (x86)* with *ProgramFiles*.
* click *Save* and restart Sahi.  

### Add more browsers to Sahi Dashboard
To add browsers to Sahi, open _**SAKULI_HOME**\sahi\userdata\config\browser_types.xml_. Each browser is defined within a **browserType** block. 
Copy one and add a new section pointing to the executable path.

Remark: <path> is defined with **"$ProgramFiles (x86)…"**, which is wrong. If your browsers does not appear in the dashboard, try to delete the " (x86)"-part of each line and restart the Sahi dashboard.

