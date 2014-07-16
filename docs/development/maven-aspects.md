# Maven aspects in the Sakuli project

## Default lifecycle actions
### Complete build process `mvn clean deploy`
 1. Compiles the sources with the `aspectj-maven-plugin` to weave the source code and the dependencies
 2. Executes all unit tests
 3. Adds the built maven artifacts to the local workspace
 4. Builds the `sakuli-zipped-release-vX.X.X.zip` file locally
 5  Executes all **integration tests** (with UI-Tests)
 6. Deploys the maven artifacts to the local repository
 
### Run unit tests `mvn test`
 Runs all steps until step **2.**
 
### Run integration tests `mvn verify`
 Runs all steps until step **5.**
 
### Build a new release `mvn release:perform`
 See the instruction in the document [How to Release](how-to-release.md)
 
## Special maven profiles
Profiles can be added with option `-P`, followed by a parameter, e.g. 

	mvn install -P no-ui-tests,upload-release

* `no-ui-tests` Disables the UI based test in phase **integration-test**
* `upload-release` Copies the generated `sakuli-zipped-release-vX.X.X.zip` file and maven artifacts to the [ConSol Labs](http://labs.consol.de/sakuli/) server. 
<!--FIXME: wenn default active, wie deaktiviert man das? -->
* `generate-jsdox` (default: active) If this profile will be disabled the module **docs** won't generate the files in folder [Sakuli-API](../api).
<!--FIXME: unklar-->
* `jsdox-set-proxy` If it is necessary to run the [Sakuli-API](../api) build behind a company proxy, enable this profile and configure your
 proxy in the file [docs/pom.xml](../pom.xml).
* `release-build` (internal use)
 This profile will be only enabled if you perform a release, see [How to Release](how-to-release.md). In this case it is necessary to override the path of the sahi installation.
 
                                                                   