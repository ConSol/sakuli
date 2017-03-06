* check TODOs
* make stable base image for vnc containers after https://github.com/ConSol/docker-headless-vnc-container/issues/11
* Example: consol/sakuli-ubuntu-icewm:v1.1.0-SNAPSHOT-218_docker_usermod_openshift

S.M.:
* Use a prebuild hook to set the current branch name as BUILD_ARG =>
  no need for update_sakuli_version.sh
  (see https://github.com/ConSol/sakuli/blob/github-204-docker-cloud/docker/omd-labs-centos-sakuli/hooks/build)
