# How to prepare a new release

## (Jenkins CI) Perform a new official release
* Go to [Jenkins - Sakuli_perform_release](http://labs-build.consol.de/job/Sakuli_perform_release/) and execute the job and set the parameters `REL_VERSION` and `DEV_VERSION`
* Update the tag `stable` at [read-the-docs](https://readthedocs.org/projects/sakuli/).
* Create a new taged verison on [DockerHub](https://hub.docker.com/) with same `REL_VERSION` for the following images:
	* [consol/sakuli-centos-xfce](https://hub.docker.com/r/consol/sakuli-centos-xfce/~/settings/automated-builds/)
	* [consol/sakuli-ubuntu-xfce](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce/~/settings/automated-builds/)

## (local) Process a new release with maven

### 1. Dry-run
Generate a new branch (if needed):

		mvn release:branch        

Do a dry-run _without_ generating any new files or commits:

		mvn release:prepare -DdryRun=true

### 2. Preparation
Generate a new release ZIP file and add it to the git repository. Also a new __git tag__ will be created. If you want to generate a new branch automatically, look for the following line in `pom.xml`:

	<!-- config with separate branch -->
    <preparationGoals>clean scm:branch assembly:single -Dincludes=install/*.zip scm:add </preparationGoals>

Run maven:

	mvn release:prepare

 __On errors:__ see __On general errors__

### 3. Release
Commit the prepared release ZIP file and the new POMs.

	mvn release:perform

### 4. Final cleanup
If all went good, remove the pom rollback files:

	mvn release:clean`

Push your changes and merge your branch with the master branch:

    git checkout master
    git merge <branchName>
    git push

Finally delete the no longer needed remote feature branch:

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
* rerun the release preparation with `mvn release:clean release:prepare`
   __OR__ make a general release rollback over `mvn release:rollback`

* Check your maven settings in `~/.m2/settings.xml`:
   * Ensure that your private key has access rights for user 'sakuli@labs.consol.de'
   * Ensure that your private key is added to ssh-agent:
     ```
     ssh-add <path-to-key>
     ```
   * Check proxy settings - the server `labs.consol.de` must be reachable'
   * Ensure that the ConSol-Labs server is configured correctly, see [Installation Developers](installation-developers.md#database-setup)
