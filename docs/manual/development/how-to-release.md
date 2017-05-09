# How to prepare a new release

* Check if all features are merged in dev
* Check if `changelog.md` is correct

## (Jenkins CI) Perform a new official release
* Go to [Jenkins - Sakuli_perform_release](http://labs-build.consol.de/job/Sakuli_perform_release/) and execute the job and set the parameters `REL_VERSION` and `DEV_VERSION`
* Update the tag `stable` at [read-the-docs](https://readthedocs.org/projects/sakuli/).
* Create a new taged verison on [DockerHub](https://hub.docker.com/) with same `REL_VERSION` for the following images:
	* [consol/sakuli-centos-xfce](https://hub.docker.com/r/consol/sakuli-centos-xfce/~/settings/automated-builds/)
	* [consol/sakuli-centos-xfce](https://hub.docker.com/r/consol/sakuli-centos-xfce-java/~/settings/automated-builds/)
	* [consol/sakuli-ubuntu-xfce](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce/~/settings/automated-builds/)
	* [consol/sakuli-ubuntu-xfce](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce-java/~/settings/automated-builds/)
* Add `sakuli-vXXX-installer.jar` and `sakuli-vXXX.zip` from https://labs.consol.de/sakuli/install/ to a new release XXX at https://github.com/ConSol/sakuli/releases
* Check if maven artifacts have been deployed at https://labs.consol.de/maven/repository/org/sakuli/ 
* Copy the recent content of the `changelog.md` to the release tag at https://github.com/ConSol/sakuli/releases
* Test the new docker images as long the [sakuli-exampels](https://github.com/ConSol/sakuli-examples) are not in the automated build

## (local) Process a new release with maven

### 1. Dry-run (if needed)
Generate a new relase over **jgitflow-maven-plugin** (branch: `release/xxx`):

		mvn jgitflow:release-start -DnoDeploy=true -DpushReleases=false -DlocalOnly=true      

Do a dry-run _without_ deploying and pushing any new files:

		mvn jgitflow:release-finish -DnoDeploy=true -DpushReleases=false -DlocalOnly=true

Delete all local git changes and branches

### 2. Release

Generate a new relase over **jgitflow-maven-plugin** (branch: `release/xxx`):

		mvn jgitflow:release-start

Execute the following commands, to update the Dockerfiles to the new release version at the branch `release/xxx` .
        
        docker/update_sakuli_version.sh --new-version $REL_VERSION
        git commit -am "update Dockerfiles to REL-Version v$REL_VERS"
        

Start the release deploying and pushing any new branches and tags if needed:

		mvn jgitflow:release-finish

Go to the `dev` branch and adjust the version of the Dockerfiles:

        docker/update_sakuli_version.sh --new-version $DEV_VERS
        git commit -am "update Dockerfiles to DEV-Version v$DEV_VERS"

Push your changes:

    git push --all

Finally delete the no longer needed remote feature branch and do the manual release steps from above (after Jenkins job):

	git branch -d <branchName>
    git push origin --delete <branchName>


## Troubleshooting
### Re-use a tag
To re-use an already set tag, remove it from git (local and remote):

    # delete the tag locally:
    git tag -d sakuli-X.X.X`
	# push to remote:
	git push --delete origin sakuli-X.X.X`

### General

* Check your maven settings in `~/.m2/settings.xml`:
   * Ensure that your private key has access rights for user 'sakuli@labs.consol.de'
   * Ensure that your private key is added to ssh-agent:
     ```
     ssh-add <path-to-key>
     ```
   * Check proxy settings - the server `labs.consol.de` must be reachable'
   * Ensure that the ConSol-Labs server is configured correctly, see [Installation Developers](installation-developers.md#database-setup)
