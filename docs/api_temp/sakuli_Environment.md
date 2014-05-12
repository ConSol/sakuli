Environment(`optResumeOnException`)
-----------------------------------
Represents an Environment
CONSTRUCTOR: Environment


**Parameters**

**`optResumeOnException`**:  *Boolean*,  if this parameter is undefined, it will be false.

setSimilarity(`similarity`)
---------------------------
set a new default similarity for the screen capturing methods.


**Parameters**

**`similarity`**:  *Double*,  value between 0 and 1, default = 0.8f

**Returns**

this Environment or NULL on errors.

getRegionFromFocusedWindow()
----------------------------
or NULL on errors.


**Returns**

a Region object from the current focused window

takeScreenshot(`pathName`)
--------------------------
Takes a screenshot of the current screen and saves it to the overgiven path.
If there ist just a file name, the screenshot will be saved in your testsuite log folder.

for example "test.png".


**Parameters**

**`pathName`**:  *String*,  "pathname/filname.format" or just "filename.format"<br>

sleep(`seconds`)
----------------
Blocks the current testcase execution for x seconds



**Parameters**

**`seconds`**:  *Integer*,  to sleep

**Returns**

this Environment or NULL on errors.

getClipboard()
--------------
**Returns**

the current content of the clipboard as String or NULL on errors

setClipboard(`text`)
--------------------
sets the String paramter to the system clipboard



**Parameters**

**`text`**:  *String*,  text as string

**Returns**

this Environment.

pasteClipboard()
----------------
pastes the current clipboard content into the focused area.
Will do the same as "STRG + C".



**Returns**

this Environment.

copyIntoClipboard()
-------------------
copy the current selected item or text to the clipboard.
Will do the same as "STRG + V".



**Returns**

this Environment.

paste(`text`)
-------------
pastes the text at the current position of the focus/carret <br/>using the
clipboard and strg/ctrl/cmd-v (paste keyboard shortcut)



**Parameters**

**`text`**:  *String*,  a string, which might contain unicode characters

**Returns**

this Environment or NULL on errors.

pasteMasked(`text`)
-------------------
makes a masked paste(String) without any logging.



**Parameters**

**`text`**:  *String*,  a string, which might contain unicode characters

**Returns**

this Environment or NULL on errors.

pasteAndDecrypt(`text`)
-----------------------
combines pasteMasked(String) and decryptSecret(String).



**Parameters**

**`text`**:  *String*,  encrypted secret

**Returns**

this Environment or NULL on errors.

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

this Environment or NULL on errors.

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

this Environment or NULL on errors.

typeAndDecrypt(`text`, `optModifiers`)
--------------------------------------
Decrypt and enters the given text one character/key after another using keyDown/keyUp.
The entered text will be masked at the logging. For the details of the decryption see decryptSecret(String).
<p/>
About the usable Key constants see documentation of Key.
The function use a subset of a US-QWERTY PC keyboard layout to type the text.
The text is entered at the current position of the focus.



**Parameters**

**`text`**:  *String*,  containing characters and/or Key constants

**`optModifiers`**:  *String*,  (optional) an String with only Key constants.

**Returns**

this Environment or NULL on errors.

decryptSecret(`secret`)
-----------------------
Decrypt a encrypted secret and returns the value at runtime.
The decryption will only work if the encryption and decryption happen on the same physical machine.
There will be no logging with the decrypted secret during this step.
<p/>
To create a encrypted secret see "README.txt".



**Parameters**

**`secret`**:  *String*,  encrypted secret as String

**Returns**

decrypted String

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

logError(`message`)
-------------------
make a error-log over Java backend into the log file.
This won't stop the execution of the test case.
The log entries can be configured over the properties "log4.properties"



**Parameters**

**`message`**:  *String*,  as a String

logWarning(`message`)
---------------------
make a debug-log over Java backend into the log file.
The log entries can be configured over the properties "log4.properties"



**Parameters**

**`message`**:  *String*,  as a String

logInfo(`message`)
------------------
make a info-log over Java backend into the log file.
The log entries can be configured over the properties "log4.properties"



**Parameters**

**`message`**:  *String*,  as a String

logDebug(`message`)
-------------------
make a debug-log over Java backend into the log file.
The log entries can be configured over the properties "log4.properties"



**Parameters**

**`message`**:  *String*,  as a String

