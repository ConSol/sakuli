TestCase(warningTime, criticalTime)
-----------------------------------
Set as default folder the current test case folde and calls


**Parameters**

**warningTime**,  threshold in seconds

**criticalTime**,  threshold in seconds

TestCaseWithImagePath(warningTime, criticalTime, picPathArray)
--------------------------------------------------------------
This function initialize the Sakuli object and set the warning and critical time for this test case;



**Parameters**

**warningTime**,  threshold in seconds

**criticalTime**,  threshold in seconds

**picPathArray**,  a path to the folder, which contains the picture for these test case.

**Returns**

***,  a initialized Sakuli object.

TestCaseWithImagePathArray(warningTime, criticalTime, picPathArray)
-------------------------------------------------------------------
This function initialize the Sakuli object and set the warning and critical time for this test case;



**Parameters**

**warningTime**,  threshold in seconds

**criticalTime**,  threshold in seconds

**picPathArray**,  an Array of Paths to the folder, which contains all the pictures for these test case.

**Returns**

***,  a initialized Sakuli object.

endOfStep(stepName, warningTime)
--------------------------------
adds a step over the java backend into the current test case and stores it to the database.


**Parameters**

**stepName**,  


**warningTime**,  


handleException(e)
------------------
handles with any Exception or Error. The handleException function calls the Java backend and stores the exception to the database.

The normal usage is at the end of a try-block in the cach-block like this:

try{
... do something
} catch (e) {
sakuli.handleException(e);
}



**Parameters**

**e**,  any Exception or Error

saveResult()
------------
save the results of the current test case to the database.
should be called in finally-block of the test case:
try{
... do something
} catch (e) {
sakuli.handleException(e);
} finally {
sakuli.saveResult();
}



getID()
-------
returns the current id of this test case.


**Returns**

***,  String

init()
------
