Sakuli Api
==========

This Page give you informations about the structur and an overview about some important Sakuli functions.
Use the [Sakuli-API][1] get further informations about the features of Sakuli.


Command structure<br />
------------------
#### `Testssuite`<br />
First you have to define a suite for your porject, the suite contains properties to the environment, your test cases and your test steps
#### `Testcase`<br />
You can define test cases in your suite with __*.sah__ files
#### `Teststep`<br />
You can organize your test cases in different steps, this will help you to structure your test case and will give you more details especially for troubleshooting


Initialize a test suite<br />
------------------------------
* Initialize your test suite in your project in __'testsuite.suite'__<br />
* You can customize your testsuite in __'testsuite.properties'__<br />

Ways initialize test case(s)<br />
------------------------------------

__Initialize a test case with warning and critical time__ <br />
>#### `TestCase(warningTime, criticalTime)`<br />
>`warningTime`<br />
>Time in sec. for a warning message <br />
>`criticalTime`<br />
>Time in sec. for a critical message <br />

__Initalize a test case with warning time, critical time and a path to the folder you want to use to build your test case with__<br />
>#### `TestCaseWithImagePath(warningTime, criticalTime, picPath)`<br />
>`picPath`<br />
>Pic path of the pic source folder <br />

__Initalize a test case with warning time, critical time and a paths to the folders you want to use to build your test case with. Please note that the folders will be internal matched up, therefor you should avoid using the same name for a pic file__<br />
>#### `TestCaseWithImagePathArray(warningTime, criticalTime, picPathArray)`<br />
>`picPathArray`<br />
>An Array of Paths to the folder, which contains all the pictures for these test case <br />

__Saves the results of the current test case to the database. Should be called in finally-block of the test case__<br />
>#### `testCase.saveResult()`<br />


__Handles with any exception or error__<br />
> #### `testCase.handleException(e)`<br />
>`e`<br />
>Any exception or error <br />

Ways to describe test step(s)<br />
------------------------------------

__Adds a step over the java backend into the current test case and stores it to the database__<br />
>#### `testCase.endOfStep(stepName, warningTime)`<br />
>`stepName`<br />
>Name the step<br />
>`warningTime`<br />
>Time in sec. for a warning message<br />

[1]: http://htmlpreview.github.io/?https://github.com/ConSol/sakuli/blob/master/docs/api/index.html
