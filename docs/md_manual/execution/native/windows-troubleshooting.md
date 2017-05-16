If you have some errors with your Windows installation, you can check the following points:

#### Change Windows theme and title bar colors
Windows 7 comes by default with an "aero" theme, which is quite awkward for Sikuli, because there are many transparency effects which cause window elements to change their appearance dependend on the elements below. For that, change the theme to "Windows Classic".
![classic](images/w_classictheme.jpg)


Furthermore, change the colors of **active** and **inactive** title bars to **non gradient**: 
![titlebars](images/w_titlebar.jpg)

#### RDP related settings
The following steps have only to be done if you are accessing the Sakuli Client via RDP. 
##### Disable Clipboard Sharing
The "paste" function of Sakuli uses the clipboard at runtime to decrypt and paste passwords. For this reason, the clipboard exchange of the Sakuli client and the RDP client should be suppressed in the settings tab of your **local Remote Desktop client**:

![clipboard](images/w_clipboard.jpg)

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
