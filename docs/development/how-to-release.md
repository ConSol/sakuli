How to prepare a new release
============================

## Process a new release over maven


### 1. simulate a new release
* generate new branch (if needed) `mvn release:branch`
        
* run maven with `mvn release:prepare -DdryRun=true`

* __result:__ it just makes a simulation _without_ generating any new files or commits.

### 2. prepare a new release
* run maven with

    `mvn release:prepare`

* __result:__ generates a new zipped release file and add it to the git repository. Also an new __git tag__ will be created
     * if you want to generate a new branch automatically  see in the `pom.xml` with the following line

        ```
        <!-- config with separate branch -->
        <preparationGoals>clean scm:branch assembly:single -Dincludes=install/*.zip scm:add </preparationGoals>
        ```

* __on errors:__ see - __On general errors__

### 3. perform a new release
* run maven with

    `mvn release:perform`

* __result:__ the prepared release-zip-file and the new POMs will be committed

### 4. final cleanup
* if all went good, remove the pom rollback files with `mvn release:clean`
* push your changes and merge your branch with the master branch

    ```
    git chekout master
    git merge <branchName>
    git push
    ```

* finally delete the no longer needed  remote feature branch
    * __local:__  `git branch -d <branchName>`
    * __remote:__  `git push origin --delete <branchName>`

- - -

## On general errors
1. Remove allready set tags from git (local and remote) to use the same tag again:
    * __local:__ `git tag -d sakuli-X.X.X`
    * __remote:__ `git push --delete origin sakuli-X.X.X`

2. rerun the release preparation with `mvn release:clean release:prepare`
   __OR__ make a general release rollback over `mvn release:rollback`
   
3. Check your maven settings in `~/.m2/settings.xml`:
   * Ensure that your private key has access rights for user 'sakuli@labs.consol.de'
   * Proxy Settings: server 'labs.consol.de' must be reachable'
   
     ```
     
     <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                           http://maven.apache.org/xsd/settings-1.0.0.xsd">
     <proxies>
       <proxy>
           <id>proxy</id>
           <active>true</active>
           <protocol>http</protocol>
           <host>proxy.your-company.com</host>
           <port>8080</port>
           <nonProxyHosts>localhost</nonProxyHosts>
       </proxy>
     </proxies>
     
         <servers>
             <server>
                 <id>labs.consol.de</id>
                 <username>sakuli</username>
                 <privateKey>${user.home}/.ssh/key</privateKey>
             </server>
         </servers>
     
     </settings>
     
     ```


