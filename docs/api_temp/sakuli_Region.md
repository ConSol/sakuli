Region(`optResumeOnException`)
------------------------------
CONSTRUCTOR: Region - Represents a region.


**Parameters**

**`optResumeOnException`**:  *Boolean*,  if this parameter is undefined, it will be false.

RegionImage(`optResumeOnException`, `imageName`)
------------------------------------------------
CONSTRUCTOR: Region - Represents a region.


**Parameters**

**`optResumeOnException`**:  *Boolean*,  if this parameter is undefined, it will be false.

**`imageName`**:  *String*,  name of the image pattern

RegionRectangle(`optResumeOnException`, `x`, `y`, `w`, `h`)
-----------------------------------------------------------
CONSTRUCTOR: Region - Represents a region.


**Parameters**

**`optResumeOnException`**:  *Boolean*,  if this parameter is undefined, it will be false.

**`x`**:  *Integer*,  – x position of a rectangle.

**`y`**:  *Integer*,  – y position of a rectangle.

**`w`**:  *Integer*,  – height of a rectangle.

**`h`**:  *Integer*,  – width of a rectangle.

loadRegion(`javaObject`, `resumeOnException`)
---------------------------------------------
Loads a region


**Parameters**

**`javaObject`**,  


**`resumeOnException`**,  


**Returns**

*{*,  }

find(`optImageName`)
--------------------
Finds an image inside this region immediately.

(if not set, the find operation will take place on the predefined region object.)


**Parameters**

**`optImageName`**:  *String*,  name of the preloaded picture

**Returns**

the found Region or if the target can't be found  null.

findRegion()
------------
Finds a target in this Region immediately;



**Returns**

the found Region or if the target can't be found null.

exists(`optImageName`, `optWaitSeconds`)
----------------------------------------
Check whether the give pattern is visible on the screen.



**Parameters**

**`optImageName`**:  *String*,  if set, the function search inside the given region for the image

**`optWaitSeconds`**:  *Integer*,  if set, the funciton search for x seconds for the pattern.

**Returns**

this Region or null

click()
-------
makes a mouse click on the center of the Region.



**Returns**

the Region or NULL on errors.

doubleClick()
-------------
makes a double click on the center of the Region.



**Returns**

the Region or NULL on errors.

rightClick()
------------
makes a rigth click on the center of the Region.



**Returns**

the Region or NULL on errors.

waitForImage(`imageName`, `seconds`)
------------------------------------
Blocks and waits until a target which is specified by the optImageName is found in the hole
Screen within a given time period in seconds.

or null if the target can not be found within the given time.


**Parameters**

**`imageName`**:  *String*,  name of the image pattern

**`seconds`**:  *Integer*,  the maximum time to waitFor in seconds

**Returns**

a Region object representing the region occupied by the found target,

waitFor(`seconds`)
------------------
same function as waitForImage, just waiting for the predefined pattern.

or null if the target can not be found within the given time.


**Parameters**

**`seconds`**:  *Integer*,  the maximum time to waitFor in seconds

**Returns**

a Region object representing the region occupied by the found target,

paste(`text`)
-------------
pastes the text at the current position of the focus/carret <br/>using the
clipboard and strg/ctrl/cmd-v (paste keyboard shortcut)



**Parameters**

**`text`**:  *String*,  a string, which might contain unicode characters

**Returns**

this Region or NULL on errors.

pasteMasked(`text`)
-------------------
makes a masked paste(String) without any logging.



**Parameters**

**`text`**:  *String*,  a string, which might contain unicode characters

**Returns**

this Region or NULL on errors.

pasteAndDecrypt(`text`)
-----------------------
combines pasteMasked(String) and decryptSecret(String).



**Parameters**

**`text`**:  *String*,  encrypted secret

**Returns**

this Region or NULL on errors.

type(`text`, `optModifiers`)
----------------------------
Enters the given text one character/key after another using keyDown/keyUp.
<p/>
About the usable Key constants see documentation of Key.
The function use a subset of a US-QWERTY PC keyboard layout to type the text.
The text is entered at the current position of the focus.



**Parameters**

**`text`**:  *String*,  containing characters and/or Key constants

**`optModifiers`**:  *String*,  (optional) an String with only Key constants.

**Returns**

this Region or NULL on errors.

typeMasked(`text`, `optModifiers`)
----------------------------------
Enters the given text one character/key after another using keyDown/keyUp.
The entered text will be masked at the logging.
<p/>
About the usable Key constants see documentation of Key.
The function use a subset of a US-QWERTY PC keyboard layout to type the text.
The text is entered at the current position of the focus.



**Parameters**

**`text`**:  *String*,  containing characters and/or Key constants

**`optModifiers`**:  *String*,  (optional) an String with only Key constants.

**Returns**

this Region or NULL on errors.

typeAndDecrypt(`text`, `optModifiers`)
--------------------------------------
Decrypt and enters the given text one character/key after another using keyDown/keyUp.
The entered text will be masked at the logging. For the deatails of the decryption see decryptSecret(String).
<p/>
About the usable Key constants see documentation of Key.
The function use a subset of a US-QWERTY PC keyboard layout to type the text.
The text is entered at the current position of the focus.



**Parameters**

**`text`**:  *String*,  containing characters and/or Key constants

**`optModifiers`**:  *String*,  (optional) an String with only Key constants.

**Returns**

this Region or NULL on errors.

deleteChars(`amountOfChars`)
----------------------------
delete a amount of chars in a field



**Parameters**

**`amountOfChars`**:  *Integer*,  number of chars to delete

**Returns**

this Region or null on errors

mouseWheelDown(`steps`)
-----------------------
move the mouse pointer to the given target location and move the
wheel the given steps down.



**Parameters**

**`steps`**:  *Integer*,  the number of steps

mouseWheelUp(`steps`)
---------------------
move the mouse pointer to the given target location and move the
wheel the given steps up.



**Parameters**

**`steps`**:  *Integer*,  the number of steps

move(`offsetX`, `offsetY`)
--------------------------
Set a offset to a specific Region and returns the new Region object.
The offset function will move the Region's rectangle with x to the right and with y to the left.
The size of the rectangle will be the same.



**Parameters**

**`offsetX`**,  x-value for the offset action

**`offsetY`**,  y-value for the offset action

**Returns**

a Region with the new coordinates

grow(`range`)
-------------
create a region enlarged range pixels on each side



**Parameters**

**`range`**,  of pixels

**Returns**

a new Region

grow(`width`, `height`)
-----------------------
create a region with enlarged range pixels



**Parameters**

**`width`**,  in pixels to grow in both directions

**`height`**,  in pixels to grow in both directions

**Returns**

a new Region

above()
-------
with a height of range number of pixels.


**Returns**

a new Region that is defined above the current region’s top border

below()
-------
with a height of range number of pixels.


**Returns**

a new Region that is defined below the current region’s top border

left()
------
with a height of range number of pixels.


**Returns**

a new Region that is defined on the left the current region’s top border

right()
-------
with a height of range number of pixels.


**Returns**

a new Region that is defined on the right the current region’s top border

setH()
------
set the height, based form the upper left corner downsides


getH()
------
**Returns**

height as int value

setW(`width`)
-------------
**Parameters**

**`width`**,  set the width, based form the upper left corner to the right

getW()
------
**Returns**

width as int value

setX(`x`)
---------
**Parameters**

**`x`**,  set the X coordinate of the upper left corner.

getX()
------
**Returns**

width as int value

setY()
------
**Returns**

X coordinate of the upper left corner

getY()
------
**Returns**

Y coordinate of the upper left corner

highlight(`seconds`)
--------------------
or the default time


**Parameters**

**`seconds`**,  highlights this Region for x seconds

extractText()
-------------
**Returns**

from this region a extracted Text as String

getRegionImpl()
---------------
gets the inherit java object for not yet wrapped methods


setRegionImpl(`modifiedJavaObject`)
-----------------------------------
updates the inherit java object after modifaction with getRegionImpl.


**Parameters**

**`modifiedJavaObject`**,  


