If you have some errors with your Linux installation, you can check the following points:

## install GNOME session fallback theme
Sakuli can test on Unity, of course - but [gnome-session-fallback](https://apps.ubuntu.com/cat/applications/gnome-session-fallback/) is more than sufficient…  

 `sudo apt-get install gnome-session-fallback`

After the installation, relogin and select the desktop envirionment __GNOME Flashback (Metacity)__:
![fallback](.././docs/images/u_theme_select.jpg)

![flashback](.././docs/images/u_flashback.jpg)

The Ubuntu menu bar should have changed now to the "classical" one: 

![menu](.././docs/images/u_menu.jpg)


## Restore gsettings key bindings

In headless checks you will encounter problems using the TAB key as well as "s": 
* The TAB key will switch applications (like Alt+tab)
* "s" will open the applications menu

For some reason (?), gsettings binds "s" and TAB to the Super key by default. Open a terminal as the Sakuli user and execute the following commands to restore that to the default: 

    gsettings set org.gnome.desktop.wm.keybindings switch-applications "['<Alt>Tab']"
    gsettings set org.gnome.desktop.wm.keybindings panel-main-menu "['<Alt>F1']"
