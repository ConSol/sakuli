TestCase(`warningTime`		threshold, `criticalTime`		threshold)
-------------------------------------------------------------
**Parameters**

**`warningTime`		threshold**:  *Integer*,  in seconds

**`criticalTime`		threshold**:  *Integer*,  in seconds

TestCaseWithImagePath(warningTime		threshold, criticalTime	threshold, picPathArray	a)
-------------------------------------------------------------------------------------
This function initializes the Sakuli object and sets the warning and critical time for this test case.



**Parameters**

**warningTime		threshold**:  *Integer*,  in seconds

**criticalTime	threshold**:  *Integer*,  in seconds

**picPathArray	a**:  *String[]*,  path to the folder, which contains the picture for these test cases.

**Returns**

an initialized Sakuli object.

TestCaseWithImagePathArray(Integer	warningTime		threshold, Integer	criticalTime	threshold, String\[\]	picPathArray	an)
----------------------------------------------------------------------------------------------------
This function initializes the Sakuli object and sets the warning and critical time for this test case.



**Parameters**

**Integer	warningTime		threshold**,  in seconds

**Integer	criticalTime	threshold**,  in seconds

**String[]	picPathArray	an**,  Array of Paths to the folders containing all the pictures for these test cases.

**Returns**

an initialized Sakuli object.

endOfStep(String	stepName, Integer	warningTime)
-----------------------------------------------
Adds a step over the java backend into the current test case and stores it to the database.


**Parameters**

**String	stepName**,  


**Integer	warningTime**,  


handleException(Exception	e	Any)
--------------------------------
Handles any Exception or Error. The handleException function calls the Java backend and stores the Exception to the database.

Use it at the end of a catch-block. Example:
try {
... do something
} catch (e) {
sakuli.handleException(e);
}



**Parameters**

**Exception	e	Any**,  Exception or Error

saveResult()
------------
Save the results of the current test case to the database.

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

String	id

init()
------
This function will be called automatically on the startup of TestCase().


