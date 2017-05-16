## Log how to use the sikulix-supplemenatal package:

The new build 'libVisionProxy.so' is already included in the fork [toschneck/SikuliX-2014](https://github.com/toschneck/SikuliX-2014) and the following maven dependency:

```
 <dependency>
     <groupId>com.sikulix</groupId>
     <artifactId>sikulixapi-complete</artifactId>
     <version>1.1.990/version>
 </dependency>
```
  
---

* Download under [SikuliX-2014/Setup/SikuliX-1.1.0-Beta-Supplemental-Linux.zip](https://github.com/RaiMan/SikuliX-2014/blob/master/Setup/src/main/resources/SikuliX-1.1.0-Beta-Supplemental-Linux.zip)

* Unzip and read included README

* Do the following steps.
  
  ```
  sudo apt-get install libcv-dev
  
  sudo apt-get install libtesseract-dev
  
  ./ubuntu14_openCV_symbolic_links.sh
  
  ./makeVisionProxy 
  
  ```
* replace the 'libVisionProxy.so' file
