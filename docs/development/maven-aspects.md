# Maven aspects in the Sakuli project

## Default Lifecycle actions
### Complete build process `mvn clean deploy`
 1. Compiles the sources with the `aspectj-maven-plugin` to weave the source code and the dependencies
 2. Execute all unit tests
 3. Add the built maven artifacts to the local workspace
 4. Builds the `sakuli-zipped-release-vX.X.X.zip` file locally in the folder `target\install`
 5  Execute all integration tests (with UI-Tests)
 6. Deploys the maven artifacts to the local repository
 
### Run unit tests `mvn test`
 Runs all steps until step **2.**
 
### Run integration tests `mvn verify`
 Runs all steps until step **5.**
 
### Build a new release `mvn release:perform`
 See the instruction in the document [How to Release](how-to-release.md)
 
## Special Maven Profiles
Profiles cann be added with `-P` parameter, like e.g. `mvn install -P no-ui-tests,upload-release`

#### `no-ui-tests`
Disables in the phase `integration-test` the UI based tests.
#### `upload-release`
 Enables the secure copy of the `sakuli-zipped-release-vX.X.X.zip` file and the maven artifacts to the 
 [ConSol Labs](http://labs.consol.de/sakuli/) server. Your private key for the ssh connection have to be configured in
  you maven config file `.m2/settings.xml` like follow:
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
 
#### `generate-jsdox`
 If this profile will be enabled the module **docs** and generate the markdown files in foler [Sakuli-API](../api).
  
#### `jsdox-set-proxy`
 If it is necessary to run the [Sakuli-API](../api) build behind a company proxy, enable this profile and configure your
 proxy in the file [docs/pom.xml](../pom.xml).
  
#### `release-build` (internal use)
 This profile will be only enabled if you perform a release, see [How to Release](how-to-release.md). In this case
 it is necessary to override the path of the sahi installation.
 
                                                                   
