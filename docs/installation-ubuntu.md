# Installation guide for Sakuli under Linux (Ubuntu 14.04 Desktop LTS)
You probably came from the page "Introduction" - if not, and if you are nor sure what Sakuli is, please read first [README](./README.md). 

## Prerequisites
The following installation manual assumes that...

* you have a fresh installed Ubuntu Linux 14.04 Desktop in front of you
* this machine has access to the internet
* is up to date
* has already a user account "sakuli" with sudo rights

We recommend to run Sakuli clients on virtual machines, as they are easy to manage. 

## Preparations
Before you start with the implementation of Sakuli tests, the following settings have to be done on the operating system.
### Disable desktop background 
Set the desktop background to a homogenous color. 

## Change theme
Change the theme by installing [gnome-session-fallback](https://apps.ubuntu.com/cat/applications/gnome-session-fallback/):  

 `sudo apt-get install gnome-session-fallback`

After the installation, relogin and select the desktop envirionment __GNOME Flashback (Metacity)__:
![fallback](.././docs/pics/u_theme_select.jpg)

![flashback](.././docs/pics/u_flashback.jpg)

The Ubuntu menu bar should have changed now to the "classical" one: 

![menu](.././docs/pics/u_menu.jpg)


