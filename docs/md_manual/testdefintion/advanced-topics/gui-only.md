### GUI-only tests
 
If you want to run tests which do not include any web technology, you can use phantomJS instead of firefox/chrome/IE and use  the Sahi default start URL `http://sahi.example.com/_s_/dyn/Driver_initialized`.

(Reason: Sakuli depends on Sahi running, which in turn needs a running browser instance. Using PhantomJS for this, hides the browser window completely.)
