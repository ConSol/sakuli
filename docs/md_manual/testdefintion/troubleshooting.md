## Troubleshooting

### Growing Firefox profile folder

If you experience that the Sahi profile folders `sahi0-9` of Mozilla Firefox (located in `sahi\userdata\browser\ff\profiles`) are getting bigger and bigger: this is caused by two bugs: 

* [https://bugzilla.mozilla.org/show_bug.cgi?id=85788](https://bugzilla.mozilla.org/show_bug.cgi?id=85788)
* [https://bugzilla.mozilla.org/show_bug.cgi?id=686237](https://bugzilla.mozilla.org/show_bug.cgi?id=686237)

We do not know any Firefox settings which can prevent the creation of SQLITE writeahead-logs (if *you* do, please let us know). The only pragmatic solution at the moment is to delete all SQLITE files periodically or before each Sakuli run (e.g. by running as a preHook). `%SAKULI_HOME%\bin\helper\ff_purge_profile.bat` contains an example for windows. 



### Hanging applications

If you are testing applications which tend to hang/freeze, you can run a "tidy-up"-Script by using the `-pre-Hook` option of Sakuli:   

    sakuli run __INST_DIR__/example_test_suites/example_ubuntu/ -preHook 'cscript.exe %SAKULI_HOME%\bin\helper\killproc.vbs -f %SAKULI_HOME%\bin\helper\procs_to_kill.txt'
    
In `procs_to_kill.txt` you can define which processes should be killed before Sakuli starts a new check: 

    # Full path: 
    C:\Program Files\Mozilla Firefox\firefox.exe
    C:\Program Files\Internet Explorer\iexplore.exe
    # Using wildcards (%): 
    java%sakuli.jar

### Sikuli does not recognize images

If Sikuli does not recognize regions on the screen or does recognize the wrong ones, check the following list of possible reasons: 

* **Run the client's OS on a fixed resolution:** Some applications/OS scale window elements slightly depending on the resolution. for example, if you are running Sakuli within Virtualbox, the guest OS changes its resolution as soon as you resize the VM window. The dimensions of window elements can then slightly diverge by 1-2 pixels from the screenshots taken before. This difference is small for human's eyes, but a big one for Sikuli. Make sure to disable features like "Auto-Adjust Guest Display" and set the Client's desktop to a common resolution (e.g. 1024x768). Side note: the smaller you set the resolution, the less work has to be done by Sikuli. 
* **Disable any image compression algorithms** in your screenshot capturing program (Greenshot, Shutter, …). Otherwise Sikuli will compare *compressed* pattern images with *umcompressed* image data on the screen, which will fail for sure.     
* Sikuli uses a **[similarity](http://doc.sikuli.org/region.html)** value of **0.99 by default**. That value (range: 0-0.99) determines that more than (X * 100) %  of the region pixels have to match. If Sikuli does not recognize anything or the wrong regions, try to **slightly** decrease the similarity** by changing 'sakuli.region.similarity.default' globally or inside a test e.g. with `env.setSimilarity(0.8)`. This should only be neccessary if the pattern images are of poor quality or the screen always differs slightly from the pattern images (e.g. due to compression of remote sessions like vnc). Please note that a similarity of "1" would mean that "more than 100%" of the region pixels would have to match - which is completely wrong.


### Missing keystrokes on `type("...")` or failing `paste("...")`

Sikuli keyboard events (`type()`and `paste()`) on a Sahi-controlled browser instance can get lost if they are executed at the same time when Sahi internal status requests are sent from the browser to the Sahi proxy (default: 10x per sec). 

For this reason, Sikuli type/paste methods first extend the Sahi status interval to the value of `sahi.proxy.onSikuliInput.delayPerKey` (in ms) which is long enough to execute _one_ keyboard action. For the method `type` (which is "press/release character by character""), a multiple if this value is chosen. Before leaving the paste/type method, the interval gets reset by Sakuli to the default Sahi status interval.

This setting is not needed if Sikuli does keyboard actions on GUIs not controlled by Sahi.

### `Application("...").getRegion()` returns `NULL` or an `NullPointerException`
On **Ubuntu** or other **Linux** based OS check if the packe `wmctrl` is installed. If not install it via:
	
	sudo apt-get install wmctrl
	
