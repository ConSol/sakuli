loadApplication(`javaObject`, `resumeOnException`)
--------------------------------------------------
Loads an application.


**Parameters**

**`javaObject`**:  *JavaObject*,  


**`resumeOnException`**:  *Boolean*,  


open()
------
Opens the created application.
For application with a long load time you may need to change the default sleep time with setSleepTime(...).



**Returns**

this Application object.

focus()
-------
Focuses the current application, if the application is in the background.



**Returns**

this Application object.

focusWindow(`windowNumber`)
---------------------------
Focuses a specific window of the application.



**Parameters**

**`windowNumber`**,  identifies the window

**Returns**

this Application object.

closeApp()
----------
Closes the already existing application.



**Returns**

this Application object.

setSleepTime(`seconds`)
-----------------------
Sets the sleep time in seconds of the application actions to handle with long loading times.
The default sleep time is set to 1 seconds.



**Parameters**

**`seconds`**,  sleep time in seconds

**Returns**

this Application object.

getRegion()
-----------
Creates and returns a Region object from the application.



**Returns**

a Region object.

getRegionForWindow(`windowNumber`)
----------------------------------
Creates and returns a Region object from a specific window of the application.



**Parameters**

**`windowNumber`**,  identifies the window

**Returns**

a Region object.

getName()
---------
**Returns**

the name of the current application.

class Application
-----------------
