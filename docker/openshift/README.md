# OpenShift usage of the Sakuli E2E testing and monitoring Docker images

The following content uses as example the image `consol/centos-xfce-vnc` of the Dockerfile `Dockerfile.centos.xfce`.
Fist you have to create your OpenShift project

    cd openshift                
    oc new-project my-project   

Then you can use the provided templates like follow:

## Usage

### build your own image and push it oc docker registry
    oc process -f openshift.sakuli.example.image.build.yaml -v IMAGE=sakuli-oc-image | oc create -f - 

### use images from oc docker registry
    oc process -f openshift.sakuli.example.pod.run.yaml -v IMAGE=10.0.0.X:5000/oc-project/sakuli-oc-image,E2E_TEST_NAME=oc-image-test-2 | oc create -f -

### deploy a new application based on a template
    oc process -f openshift.sakuli.example.pod.run.yaml -v E2E_TEST_NAME=single-job | oc create -f -

### delete specific application or E2E test
    oc process -f openshift.sakuli.example.pod.run.yaml -v E2E_TEST_NAME=single-job | oc delete --grace-period=5 -f -
    
### delete all running pods and configs
    oc delete dc --all && oc delete routes --all && oc delete pods --all && oc delete services --all && oc delete jobs --all

## Usage of git based test definitions through volume mounts

TODO COMMAND



If something went wrong, you maybe have to enable this volume type in the cluster:

### Configure use of `gitRepo` volumes:

Login as administrator:

    oc login -u system:admin
  
Edit the security :

    oc edit securitycontextconstraints/restricted

add `gitRepo` to `volumes`:

    volumes:
    - configMap
    - downwardAPI
    - emptyDir
    - persistentVolumeClaim
    - secret
    - gitRepo

## Contact
For questions, professional support or maybe some hints, feel free to contact us via **[testautomatisierung@consol.de](mailto:testautomatisierung@consol.de)** or open an [issue](https://github.com/ConSol/docker-headless-vnc-container/issues/new).

The guys behind:

**ConSol Software GmbH** <br/>
*Franziskanerstr. 38, D-81669 München* <br/>
*Tel. +49-89-45841-100, Fax +49-89-45841-111*<br/>
*Homepage: http://www.consol.de E-Mail: [info@consol.de](info@consol.de)*
                                                 v1.1.0-SNAPSHOT-218_docker_usermod_openshift