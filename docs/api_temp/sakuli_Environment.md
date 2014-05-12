Environment(optResumeOnException)
---------------------------------
Represents an Environment


**Parameters**

**optResumeOnException**:  *Boolean*,  if this parameter is undefined, it will be false.

setSimilarity(similarity)
-------------------------
set a new default similarity for the screen capturing methods.
<ul>
</ul>


**Parameters**

**similarity**,  double value between 0 and 1, default = 0.8f </li>

**Returns**

*his {@link Environment*,  or NULL on errors.

getRegionFromFocusedWindow()
----------------------------
or NULL on errors.


**Returns**

* {@link Region*,  object from the current focused window

takeScreenshot(pathName)
------------------------
Takes a screenshot of the current screen and saves it to the overgiven path.
If there ist just a file name, the screenshot will be saved in your testsuite log folder.

for example "test.png".


**Parameters**

**pathName**:  *String*,  "pathname/filname.format" or just "filename.format"<br>

sleep(seconds)
--------------
Blocks the current testcase execution for x seconds



**Parameters**

**seconds**,  to sleep

**Returns**

*his {@link Environment*,  or NULL on errors.

getClipboard()
--------------
**Returns**

*he current content of the clipboard as {@link String*,  or NULL on errors

setClipboard()
--------------
sets the String paramter to the system clipboard



**Parameters**

****:  *ext as {@link String*,  


**Returns**

*his {@link Environment*,  .

pasteClipboard()
----------------
pastes the current clipboard content into the focused area.
Will do the same as "STRG + C".



**Returns**

*his {@link Environment*,  .

copyIntoClipboard()
-------------------
copy the current selected item or text to the clipboard.
Will do the same as "STRG + V".



**Returns**

*his {@link Environment*,  .

paste(text)
-----------
pastes the text at the current position of the focus/carret <br/>using the
clipboard and strg/ctrl/cmd-v (paste keyboard shortcut)



**Parameters**

**text**,  a string, which might contain unicode characters

**Returns**

*his {@link Environment*,  or NULL on errors.

pasteMasked(text)
-----------------
**Parameters**

**text**,  a string, which might contain unicode characters

**Returns**

*his {@link Environment*,  or NULL on errors.

pasteAndDecrypt(text)
---------------------
**Parameters**

**text**,  encrypted secret

**Returns**

*his {@link Environment*,  or NULL on errors.

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

*his {@link Environment*,  or NULL on errors.

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

*his {@link Environment*,  or NULL on errors.

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

*his {@link Environment*,  or NULL on errors.

decryptSecret()
---------------
Decrypt a encrypted secret and returns the value at runtime.
The decryption will only work if the encryption and decryption happen on the same physical machine.
There will be no logging with the decrypted secret during this step.
<p/>
To create a encrypted secret see "README.txt".



**Parameters**

****:  *ecret encrypted secret as {@link String*,  


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

logError(message)
-----------------
make a error-log over Java backend into the log file.
This won't stop the execution of the test case.
The log entries can be configured over the properties "log4.properties"



**Parameters**

**message**,  as a String

logWarning(message)
-------------------
make a debug-log over Java backend into the log file.
The log entries can be configured over the properties "log4.properties"



**Parameters**

**message**,  as a String

logInfo(message)
----------------
make a info-log over Java backend into the log file.
The log entries can be configured over the properties "log4.properties"



**Parameters**

**message**,  as a String

logDebug(message)
-----------------
make a debug-log over Java backend into the log file.
The log entries can be configured over the properties "log4.properties"



**Parameters**

**message**,  as a String

