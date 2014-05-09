Region(optResumeOnException)
----------------------------
Represents a region.


**Parameters**

**optResumeOnException**,  if this parameter is undefined, it will be false.

RegionImage(optResumeOnException, imageName)
--------------------------------------------
Represents a region.


**Parameters**

**optResumeOnException**,  if this parameter is undefined, it will be false.

**imageName**,  name of the image pattern

RegionRectangle(optResumeOnException, x, y, w, h)
-------------------------------------------------
Represents a region.


**Parameters**

**optResumeOnException**,  if this parameter is undefined, it will be false.

**x**,  – x position of a rectangle.

**y**,  – y position of a rectangle.

**w**,  – height of a rectangle.

**h**,  – width of a rectangle.

loadRegion(javaObject, resumeOnException)
-----------------------------------------
Loads a region


**Parameters**

**javaObject**,  


**resumeOnException**,  


**Returns**

*{*,  }

find(optImageName)
------------------
Finds an image inside this region immediately.

(if not set, the find operation will take place on the predefined region object.)


**Parameters**

**optImageName**,  name of the preloaded picture

**Returns**

*he found {@link Region*,  or if the target can't be found {@code null}.

findRegion()
------------
**Returns**

*he found {@link Region*,  or if the target can't be found {@code null}.

exists(optImageName, optWaitSeconds)
------------------------------------
Check whether the give pattern is visible on the screen.



**Parameters**

**optImageName**,  if set, the function search inside the given region for the image

**optWaitSeconds**,  if set, the funciton search for x seconds for the pattern.

**Returns**

*his {@link Region*,  or null

click()
-------
**Returns**

*he {@link Region*,  or NULL on errors.

doubleClick()
-------------
**Returns**

*he {@link Region*,  or NULL on errors.

rightClick()
------------
**Returns**

*he {@link Region*,  or NULL on errors.

waitForImage(imageName, seconds)
--------------------------------
Blocks and waits until a target which is specified by the optImageName is found in the hole

or null if the target can not be found within the given time.


**Parameters**

**imageName**,  name of the image pattern

**seconds**,  the maximum time to waitFor in seconds

**Returns**

* {@link Region*,  object representing the region occupied by the found target,

waitFor(seconds)
----------------
or null if the target can not be found within the given time.


**Parameters**

**seconds**,  the maximum time to waitFor in seconds

**Returns**

* {@link Region*,  object representing the region occupied by the found target,

paste(text)
-----------
pastes the text at the current position of the focus/carret <br/>using the
clipboard and strg/ctrl/cmd-v (paste keyboard shortcut)



**Parameters**

**text**,  a string, which might contain unicode characters

**Returns**

*his {@link Region*,  or NULL on errors.

pasteMasked(text)
-----------------
**Parameters**

**text**,  a string, which might contain unicode characters

**Returns**

*his {@link Region*,  or NULL on errors.

pasteAndDecrypt(text)
---------------------
**Parameters**

**text**,  encrypted secret

**Returns**

*his {@link Region*,  or NULL on errors.

type(constants, constants.)
---------------------------
Enters the given text one character/key after another using keyDown/keyUp.
<p/>
The function use a subset of a US-QWERTY PC keyboard layout to type the text.
The text is entered at the current position of the focus.



**Parameters**

**constants**:  *ext containing characters and/or {@link Key*,  


**constants.**:  *ptModifiers (optional) an String with only {@link Key*,  


**Returns**

*his {@link Region*,  or NULL on errors.

typeMasked(constants, constants.)
---------------------------------
Enters the given text one character/key after another using keyDown/keyUp.
The entered text will be masked at the logging.
<p/>
The function use a subset of a US-QWERTY PC keyboard layout to type the text.
The text is entered at the current position of the focus.



**Parameters**

**constants**:  *ext containing characters and/or {@link Key*,  


**constants.**:  *ptModifiers (optional) an String with only {@link Key*,  


**Returns**

*his {@link Region*,  or NULL on errors.

typeAndDecrypt(constants, constants.)
-------------------------------------
Decrypt and enters the given text one character/key after another using keyDown/keyUp.
<p/>
The function use a subset of a US-QWERTY PC keyboard layout to type the text.
The text is entered at the current position of the focus.



**Parameters**

**constants**:  *ext containing characters and/or {@link Key*,  


**constants.**:  *ptModifiers (optional) an String with only {@link Key*,  


**Returns**

*his {@link Region*,  or NULL on errors.

deleteChars(amountOfChars)
--------------------------
delete a amount of chars in a field



**Parameters**

**amountOfChars**,  number of chars to delete

**Returns**

*his {@link Region*,  or null on errors

mouseWheelDown(steps)
---------------------
move the mouse pointer to the given target location and move the
wheel the given steps down.



**Parameters**

**steps**,  the number of steps

mouseWheelUp(steps)
-------------------
move the mouse pointer to the given target location and move the
wheel the given steps up.



**Parameters**

**steps**,  the number of steps

move(offsetX, offsetY)
----------------------
The offset function will move the Region's rectangle with x to the right and with y to the left.
The size of the rectangle will be the same.



**Parameters**

**offsetX**,  x-value for the offset action

**offsetY**,  y-value for the offset action

**Returns**

* {@link Region*,  with the new coordinates

grow(range)
-----------
create a region enlarged range pixels on each side



**Parameters**

**range**,  of pixels

grow(width, height)
-------------------
create a region with enlarged range pixels



**Parameters**

**width**,  in pixels to grow in both directions

**height**,  in pixels to grow in both directions

above()
-------
with a height of range number of pixels.


**Returns**

* new {@link Region*,  that is defined above the current region’s top border

below()
-------
with a height of range number of pixels.


**Returns**

* new {@link Region*,  that is defined below the current region’s top border

left()
------
with a height of range number of pixels.


**Returns**

* new {@link Region*,  that is defined on the left the current region’s top border

right()
-------
with a height of range number of pixels.


**Returns**

* new {@link Region*,  that is defined on the right the current region’s top border

setH()
------
set the height, based form the upper left corner downsides


getH()
------
**Returns**

height as int value

setW()
------
set the width, based form the upper left corner to the right


getW()
------
**Returns**

width as int value

setX()
------
set the X coordinate of the upper left corner.


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

highlight()
-----------
or the default time


extractText()
-------------
getRegionImpl()
---------------
gets the inherit java object for not yet wrapped methods


setRegionImpl()
---------------
