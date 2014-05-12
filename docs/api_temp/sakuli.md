TestCase(`warningTime`, `criticalTime`)
---------------------------------------
Sets the current test case folder as default folder and calls TestCaseWithImagePathArray(...).
CONSTRUCTOR: TestCase


**Parameters**

**`warningTime`**:  *Integer*,  threshold in seconds

**`criticalTime`**:  *Integer*,  threshold in seconds

TestCaseWithImagePath(`warningTime`, `criticalTime`, `picPath`)
---------------------------------------------------------------
This function initializes the Sakuli object and sets the warning and critical time for this test case.
CONSTRUCTOR: TestCase



**Parameters**

**`warningTime`**:  *Integer*,  threshold in seconds

**`criticalTime`**:  *Integer*,  threshold in seconds

**`picPath`**:  *String*,  Path to the folder containing the pictures for these test cases.

**Returns**

an initialized Sakuli object.

TestCaseWithImagePathArray(`warningTime`, `criticalTime`, `picPathArray`)
-------------------------------------------------------------------------
This function initializes the Sakuli object and sets the warning and critical time for this test case.



**Parameters**

**`warningTime`**:  *Integer*,  threshold in seconds

**`criticalTime`**:  *Integer*,  threshold in seconds

**`picPathArray`**:  *String[]*,  An Array of Paths to the folders containing all the pictures for these test cases.

**Returns**

an initialized Sakuli object.

endOfStep(`stepName`, `warningTime`)
------------------------------------
Adds a step over the java backend into the current test case and stores it to the database.


**Parameters**

**`stepName`**:  *String*,  


**`warningTime`**:  *Integer*,  threshold in seconds

handleException(`e`)
--------------------
Handles any Exception or Error. The handleException function calls the Java backend and stores the Exception to the database.

Use it at the end of a catch-block. Example:
try {
... do something
} catch (e) {
sakuli.handleException(e);
}



**Parameters**

**`e`**:  *Exception*,  Any Exception or Error

saveResult()
------------
Saves the results of the current test case to the database.

Should be called in finally-block of the test case. Example:
try {
... do something
} catch (e) {
sakuli.handleException(e);
} finally {
sakuli.saveResult();
}


getID()
-------
Returns the current id of this test case.


**Returns**

id

init()
------
This function will be called automatically on the startup of TestCase().


