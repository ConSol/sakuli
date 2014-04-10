# !!! ONLY FOR DEVELOPEMENT:

### install the following libraries in your local maven repository
* Sahi
    install in local maven repository:

    ```
    mvn install:install-file -DgroupId=net.sf.sahi -DartifactId=ant-sahi -Dversion=4.3 -Dpackaging=jar -Dfile=core/dev_stuff/ext_libs/ant-sahi-4.3.jar
    ```

    and this also:

    ```
    mvn install:install-file -DgroupId=net.sf.sahi -DartifactId=sahi -Dversion=4.3 -Dpackaging=jar -Dfile=core/dev_stuff/ext_libs/sahi-4.3.jar
    ```


### Sikuli-X-Api
since 10.2013:

* install from local dev-stuff folder:

    ```
    mvn install:install-file -DgroupId=org.sikuli -DartifactId=SikuliX-API -Dversion=1.0.1 -Dpackaging=jar -Dfile=core/dev_stuff/ext_libs/SikuliX-API-1.0.1.jar
    ```

#### (optional) build the Sikuli-X Libraries

To get the Sikuli-X Libraries into the local repository checkout and install all Sikuli-X-Libraries
like described under:
https://github.com/RaiMan/SikuliX-API/wiki/Maven-support

To Activate the AspectJ-Features for the development environment add to the goal "aspectj:compile"
after "make" to your IDE run and debug feature. In IntelliJ go to the Maven-Plugin and make a rigth click to the
goal an select "Execute After Make".


---



### Sikuli-X-2014 (currently NOT usable see issue in [sikuli-dashboard](https://answers.launchpad.net/sikuli/+question/246410) )

    ```
    mvn install:install-file -DgroupId=org.sikuli -DartifactId=SikuliX-APIFat -Dversion=1.1.0 -Dpackaging=jar -Dfile=dev_stuff/ext_libs/sikuli-x-2014/sikulixapi.jar
    ```

* additional change pom.xml dependency
* for local download the sikulix-2014 libary to your system use:

    `git clone git@github.com:RaiMan/SikuliX-2014.git`

