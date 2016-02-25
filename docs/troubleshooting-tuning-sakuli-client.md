

# Troubleshooting and Tuning the Sakuli client

## Desktop tuning
These steps are optional, but will improve the check quality/reliability. 

### Ubuntu 
#### install GNOME session fallback theme
Sakuli can test on Unity, of course - but [gnome-session-fallback](https://apps.ubuntu.com/cat/applications/gnome-session-fallback/) is more than sufficient…  

 `sudo apt-get install gnome-session-fallback`

After the installation, relogin and select the desktop envirionment __GNOME Flashback (Metacity)__:
![fallback](.././docs/pics/u_theme_select.jpg)

![flashback](.././docs/pics/u_flashback.jpg)

The Ubuntu menu bar should have changed now to the "classical" one: 

![menu](.././docs/pics/u_menu.jpg)


#### Restore gsettings key bindings

In headless checks you will encounter problems using the TAB key as well as "s": 
* The TAB key will switch applications (like Alt+tab)
* "s" will open the applications menu

For some reason (?), gsettings binds "s" and TAB to the Super key by default. Open a terminal as the Sakuli user and execute the following commands to restore that to the default: 

    gsettings set org.gnome.desktop.wm.keybindings switch-applications "['<Alt>Tab']"
    gsettings set org.gnome.desktop.wm.keybindings panel-main-menu "['<Alt>F1']"


### Windows 

#### Change Windows theme and title bar colors
Windows 7 comes by default with an "aero" theme, which is quite awkward for Sikuli, because there are many transparency effects which cause window elements to change their appearance dependend on the elements below. For that, change the theme to "Windows Classic".
![classic](pics/w_classictheme.jpg)


Furthermore, change the colors of **active** and **inactive** title bars to **non gradient**: 
![titlebars](pics/w_titlebar.jpg)

#### RDP related settings
The following steps have only to be done if you are accessing the Sakuli Client via RDP. 
##### Disable Clipboard Sharing
The "paste" function of Sakuli uses the clipboard at runtime to decrypt and paste passwords. For this reason, the clipboard exchange of the Sakuli client and the RDP client should be suppressed in the settings tab of your **local Remote Desktop client**:

![clipboard](pics/w_clipboard.jpg)

This can be set globally in the registry **of your local host**: 

* "regedit"
* [ HKEY_CURRENT_USER\Software\Microsoft\Terminal Server Client ]
* "DisableDriveRedirection" (DWORD) => "1" 

 
##### Disable the "GUI-less" mode
If you minimize the Remote Desktop window (the window that display the remote computer’s desktop), the operating system switches the remote session to a "GUI-less mode" which does not transfer any window data anymore. As a result, Sakuli is unable to interact with the tested application’s GUI, as the whole screen is not visible.

To disable the "GUI-less" mode **on your local host**: 

* "regedit"
* [ HKEY_CURRENT_USER\Software\Microsoft\Terminal Server Client ]
* "RemoteDesktop_SuppressWhenMinimized" (DWORD) => "2"



## Troubleshooting

### Growing Firefox profile folder

If you experience that the Sahi profile folders `sahi0-9` of Mozilla Firefox (located in `sahi\userdata\browser\ff\profiles`) are getting bigger and bigger: this is caused by two bugs: 

* [https://bugzilla.mozilla.org/show_bug.cgi?id=85788](https://bugzilla.mozilla.org/show_bug.cgi?id=85788)
* [https://bugzilla.mozilla.org/show_bug.cgi?id=686237](https://bugzilla.mozilla.org/show_bug.cgi?id=686237)

We do not know any Firefox settings which can prevent the creation of SQLITE writeahead-logs (if *you* do, please let us know). The only pragmatic solution at the moment is to delete all SQLITE files periodically or before each Sakuli run (e.g. by running as a preHook). `%SAKULI_HOME%\bin\helper\ff_purge_profile.bat` contains an example for windows. 



### Hanging applications

If you are testing applications which tend to hang/freeze, you can run a "tidy-up"-Script by using the **pre-Hook** option of Sakuli:   

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
* Sikuli uses a **[similarity](http://doc.sikuli.org/region.html)** value of **0.99 by default**. That value (range: 0-0.99 determines that more than X percent of the region pixels have to match. If Sikuli does not recognize anything or the wrong regions, try to slightly **decrease the similarity**, e.g. `env.setSimilarity(0.6)`. Please note that a similarity of "1" would mean that "more than 100%" of the region pixels must match - which is completely wrong.

### Missing keystrokes on `type("...")` or failing `paste("...")`

Sikuli keyboard events (`type()`and `paste()`) on a Sahi-controlled browser instance can get lost if they are executed at the same time when Sahi internal status requests are sent from the browser to the Sahi proxy (default: 10x per sec). 

For this reason, Sikuli type/paste methods first extend the Sahi status interval to the value of `sahi.proxy.onSikuliInput.delayPerKey` (in ms) which is long enough to execute _one_ keyboard action. For the method `type` (which is "press/release character by character""), a multiple if this value is chosen. Before leaving the paste/type method, the interval gets reset by Sakuli to the default Sahi status interval.

This setting is not needed if Sikuli does keyboard actions on GUIs not controlled by Sahi.

### `Application("...").getRegion()` returns `NULL` or an `NullPointerException`
On **Ubuntu** or other **Linux** based OS check if the packe `wmctrl` is installed. If not install it via:
	
	sudo apt-get install wmctrl
	
### GUI-only tests
 
If you want to run tests which do not include any web technology, you can use phantomJS instead of firefox/chrome/IE and use  the Sahi default start URL `http://sahi.example.com/_s_/dyn/Driver_initialized`.

(Reason: Sakuli depends on Sahi running, which in turn needs a running browser instance. Using PhantomJS for this, hides the browser window completely.)
