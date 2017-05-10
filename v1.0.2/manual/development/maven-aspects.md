# Maven aspects in the Sakuli project

## Default lifecycle actions
### Complete build process `mvn clean deploy`
 1. Compiles the sources with the `aspectj-maven-plugin` to weave the source code and the dependencies
 2. Executes all unit tests
 3. Executes all **integration tests** (without UI-Tests)
 4. Adds the built maven artifacts to the local workspace
 5. Builds the `sakuli-vX.X.X.zip` file locally
 6. Deploys the maven artifacts to the local repository
 7. Deploys the maven artifacts to the remote labs.consol repository (if you have the permission for this)

### Run unit tests `mvn test`
 Runs all steps until step **2.**

### Run integration tests `mvn verify`
 Runs all steps until step **3**

### Install locally `mvn install`
 Runs all steps until step **6**

### Build a new release `mvn release:perform`
 See the instruction in the document [How to Release](how-to-release.md)

## Special maven profiles
Profiles can be added with option `-P`, followed by a parameter, e.g.

	mvn install -P upload-release

* `upload-release` Copies the generated `sakuli-zipped-release-vX.X.X.zip` file and maven artifacts to the [ConSol Labs](http://labs.consol.de/sakuli/) server. Your private key for the ssh connection have to be configured in maven config file `.m2/settings.xml`, see below
* `ui-tests` Enables the UI based test in phase **integration-test** in the modul `integration-test` and `java-dsl`.
* `generate-markdown` This profile will generate in the module **docs** the file [Sakuli-API](../testdefintion/sakuli-api.md).
   To use the profil behind a **HTTP/HTTPS** proxy, be aware that the following things are configured:
    * include in your `$M2_HOME/settings.xml` the proxy tag (if needed):
      ```
      <proxies>
          <proxy>
              <id>privoxy-config</id>
              <active>true</active>
              <protocol>http</protocol>
              <host>proxy.company.com</host>
              <port>8888</port>
          </proxy>
      </proxies>
      ```

    * configure your system **HTTP** and **HTTPS** proxy
        * for Ubuntu set in `bash.rc`:
          ```
          export http_proxy=http://proxy.company.com:8888/
          export https_proxy=http://proxy.company.com:8888/
          export ftp_proxy=http://proxy.company.com:8888/
          ```

* `release-build` (internal use)
 This profile will be only enabled if you perform a release, see [How to Release](how-to-release.md). In this case it is necessary to override the path of the sahi installation.

Example of Maven config file `.m2/settings.xml`:

  ```
      <servers>
          <server>
              <id>labs-consol-sakuli-install</id>
              <username>sakuli</username>
              <privateKey>${user.home}/.ssh/id_rsa</privateKey>
          </server>

          <server>
              <id>labs-consol-sakuli-repository</id>
              <username>maven-repository</username>
              <privateKey>${user.home}/.ssh/id_rsa</privateKey>
          </server>
      </servers>

  ```

## add new artifacts to remote repo
for example for the sahi.jars, more information see: https://maven.apache.org/guides/mini/guide-3rd-party-jars-remote.html

#### install to local repo
    mvn install:install-file -DgroupId=net.sf.sahi -DartifactId=sahi -Dversion=5.1 -Dpackaging=jar -Dfile=sahi-5.1.jar
    mvn install:install-file -DgroupId=net.sf.sahi -DartifactId=ant-sahi -Dversion=5.1 -Dpackaging=jar -Dfile=ant-sahi-5.1.jar
    mvn install:install-file -DgroupId=net.sf.sahi -DartifactId=sahi-install -Dversion=5.1 -Dpackaging=zip -Dfile=sahi-install-5.1.zip

#### install to remote repo
    mvn deploy:deploy-file -DgroupId=net.sf.sahi -DartifactId=sahi -Dversion=5.1 -Dpackaging jar -Dfile=sahi-5.1.jar -Drepository=labs-consol-sakuli-repository -Durl=scpexe://labs.consol.de/home/maven-repository/www/htdocs/repository
    
or copy the local artifacts via SCP:
    scp -r ~.m2/repository/net/sf/sahi/sahi/5.1  maven-repository@labs.consol.de:/home/maven-repository/www/htdocs/repository/net/sf/sahi/sahi/5.1