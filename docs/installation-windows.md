# installation steps for Windows 7



## environment variables 

* From the desktop, right-click *My Computer* and click *Properties*
* In *System Properties*, click on *Advanced*
	* Edit the **system variable** **%PATH%** and add one of the following paths to the java binary: 
		* Name: `PATH`
		* Value: 
			* `;C:\Program Files\Java\jre7\bin`
			* or 
			* `;C:\Program Files (x86)\Java\jre7\bin`

	* Create a new **user variable** **%SAKULI_HOME%**: 
	
		* Name: `SAKULI_HOME`
		* Value: `C:\sakuli`
		
	* Create a new **user variable** **%PATH%**: 
	
		* Name: `PATH`
		* Value: `%SAKULI_HOME%\bin\lib\libs`

Reboot after this step. 





### PhantomJS (Optional)
	
Currently, each Sakuli test will start a browser, even for pure Sikuli GUI tests (=where you don't need any browser). In that case, the headless browser *phantomJS* does the trick. 

* Download the latest version of phantomJS from [http://phantomjs.org](http://phantomjs.org)
* Open the ZIP file and copy `phantomjs.exe` to `%SAKULI_HOME%\phantomjs` (create that folder)
* Save [sahi.js](http://labs.consol.de/sakuli/install/3rd-party/phantom/sahi.js) into `%SAKULI_HOME%\phantomjs`

		<browserType> 
        	<name>phantomjs</name> 
	        <displayName>PhantomJS</displayName> 
			<icon>safari.png</icon> 
        	<path>C:\sakuli\phantomjs\phantomjs.exe</path> 
        	<options>--proxy=localhost:9999 C:\sakuli\phantomjs\sahi.js</options> 
        	<processName>phantomjs.exe</processName> 
        	<capacity>100</capacity> 
        	<force>true</force> 
    	</browserType>

Now it's time to setup the first Sakuli check: go to [First steps on Windows 7](../docs/firststeps-windows.md).

## Additional tools
### Browser 
Install any of your desired browsers (Firefox, Chrome, Opera, …) and configure them in `browser_types.xml` as described in the [troubleshooting section](../docs/troubleshooting-sakuli-client.md#no-browsers-in-dashboard).

It is recommended to install at least Mozilla Firefox. 
### Greenshot 
To take screenshots which should be used by Sikuli, you need a handy screenshot capturing tool. We highly recommend the installation of [Greenshot](http://www.getgreenshot.org), but any other tool which is able to save screenshots as JPG/PNG is possible, too. 
### Notepad++
Install an advanced text editor to edit Sakuli test cases. We recommend [Notepad++](http://notepad-plus-plus.org/).
	
