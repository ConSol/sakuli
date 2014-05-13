Sakuli Api
==========

This page gives an overview about the structure behinde Sakuli and the most important functions of Sakuli.
Use the [Sakuli-API][1] get further informations about the features of Sakuli.


Command structure
------------------
**`Testsuite`**
First you have to define a suite for your project, the suite contains properties to the environment, your test cases and your test steps
**`Testcase`**
You can define test cases in your suite with __*.sah__ files
**`Teststep`**
You can organize your test cases in different steps, this will help you to structure your test case and will give you more details especially for troubleshooting


Initialize a test suite
------------------------------
* Initialize your test suite in your project in __'testsuite.suite'__
* You can customize your testsuite in __'testsuite.properties'__

Ways initialize test case(s)
------------------------------------

__Initialize a test case with warning and critical time__ 
>#### `TestCase(warningTime, criticalTime)`
>`warningTime`
>Time in sec. for a warning message 
>`criticalTime`
>Time in sec. for a critical message 

__Initalize a test case with warning time, critical time and a path to the folder you want to use to build your test case with__
>#### `TestCaseWithImagePath(warningTime, criticalTime, picPath)`
>`picPath`
>Pic path of the pic source folder 

__Initalize a test case with warning time, critical time and a paths to the folders you want to use to build your test case with. Please note that the folders will be internal matched up, therefor you should avoid using the same name for a pic file__
>#### `TestCaseWithImagePathArray(warningTime, criticalTime, picPathArray)`
>`picPathArray`
>An Array of Paths to the folder, which contains all the pictures for these test case 

__Saves the results of the current test case to the database. Should be called in finally-block of the test case__
#### `testCase.saveResult()`


__Handles with any exception or error__
> #### `testCase.handleException(e)`
>`e`
>Any exception or error 

Ways to describe test step(s)
------------------------------------

__Adds a step over the java backend into the current test case and stores it to the database__
>#### `testCase.endOfStep(stepName, warningTime)`
>`stepName`
>Name the step
>`warningTime`
>Time in sec. for a warning message

[1]: https://github.com/ConSol/sakuli/tree/master/docs/api/
