# Installation guide for Sakuli under Windows 7
You probably came from the page "Introduction" - if not, and if you are nor sure what Sakuli is, please read first [README](https://github.com/ConSol/sakuli/blob/master/README.md). 

## Prerequisites
The following installation manual assumes that...
* you have a fresh installed Windows 7 (no matter if Home/Professional/whatever) machine in front of you
* this machine has access to the internet
* is up to date
* has already a user account "sakuli" with admin rights


## Preparations
### Disable desktop background 
Set the desktop background to a homogenous color. 

### Change theme and title bar colors
Windows 7 comes by default with an "aero" theme, which is quite awkward for SaKuli, because there are many transparency effects which cause window elements to change their appearance dependend on the elements below. For that, change the theme to "Windows Classic".
![classic](https://raw.githubusercontent.com/ConSol/sakuli/master/docs/pics/w_classictheme.jpg)
Furthermore, change the colors of active and inactive title bars to non gradient: 
![titlebars](https://raw.githubusercontent.com/ConSol/sakuli/master/docs/pics/w_titlebar.jpg)






Step 1: Run Sakuli example for your OS
-------------------------------------

Run the SakuliStarterWin.bat (TODO name) for Windows
Run the SakuliStarterUnix (TODO name) for Unix
Param - include folder


Step 2: How to create a Sakuli test
-------------------------------

* First we need to define a new suite. We make a new folder in `_sakuli_test_suite` with a name of your choice. Now we can copy and customize `testsuite.properties` and `testsuite.suite`.
* For the test case we make a new folder in the suite. In this folder we create the *.sah file for our first case, we now add this *.sah file in testsuite.suite.
* Define the *.sah
    - `_dynamicInclude($includeFolder);` include a folder that contains the sakuli libs is defined by an argument in the starter/runner script
    - `var testCase = new TestCase(60, 70);` initialize a new test case
    - `var env = new Environment();` initialize our environment
    - `var appCalc = new Application("calc.exe");` we recommend to initialize the application here
    - `var $randomString = "i am a random string";` we also recommend to initialize the variables and functions here. Note that variables only work with `$` as first sign
    - `try { ...test case ... } catch(e) { testCase.handleException(e); }`  our test case is defined inside a try/catch block, handleException(e) handles any exception or error
    - `finally { appCalc.closeApp(); testCase.saveResult(); }` inside the finally block we close our Applications and saveResult() saves our results of the current test case to the database

* Define test case
    - define test cases in steps
    - comment every step like `/************** <br /> * Open calc <br /> *************/`
    - define your test step with Sakuli functions TODO (link zu Funktionen)
    - end the step with `testCase.endOfStep("project", 20);`

* Run the test
    - we can run the test in our environment with `-run _sakuli_test_suites/example src/main/_include sahi` note that `src/main/_include` is the Sakuli-Lib we have included in our *.sah with `_dynamicInclude($includeFolder);`
    - we can use *.bat files to run the tests with windows scheduler
    - to create a *.bat with our tests we need to do ... //TODO beschreiben


Step 3: Tips and Tricks
------------------------




