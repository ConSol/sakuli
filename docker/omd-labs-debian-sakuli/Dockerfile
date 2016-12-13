# docker build --build-arg BRANCH=github-204-docker-cloud -t consol/sakuli-omd-labs .

#FROM local/omd-labs-debian
FROM consol/omd-labs-debian
MAINTAINER Simon Meggle "simon.meggle@consol.de"
ENV REFRESHED_AT 2016-12-13

ENV ANSIBLE_SAKULI /root/ansible_sakuli
ADD ansible_sakuli $ANSIBLE_SAKULI

ARG BRANCH
RUN env > /root/env

WORKDIR $ANSIBLE_SAKULI/sakuli/
# checkout the Sakuli source there
ENV SRCDIR $ANSIBLE_SAKULI/sakuli/src
# use git protocol if possible
ENV REMOTE_GIT git://github.com/ConSol/sakuli.git
# fallback
ENV REMOTE_HTTPS https://github.com/ConSol/sakuli.git
# checkout only this folder
ENV SPARSEDIR src/common/src/main/resources/org/sakuli/common/setup/omd

RUN echo "Built from $REMOTE_GIT/${BRANCH:-${SOURCE_BRANCH:-"master"}}" > /root/build_info

RUN /bin/bash -c "mkdir $SRCDIR && pushd $SRCDIR && git init &&\
  (git remote add -f origin $REMOTE_GIT || git remote set-url origin $REMOTE_HTTPS) &&\
  git fetch origin &&\
  git config core.sparseCheckout true &&\
  echo "$SPARSEDIR" > .git/info/sparse-checkout &&\
  git checkout ${BRANCH:-${SOURCE_BRANCH:-"master"}} &&\
  rm -rf $SRCDIR/.git &&\
  popd &&\
  ln -s src/$SPARSEDIR files"



# use the OMD-included Ansible to install the Sakuli stuff
ENV PYTHONPATH=/omd/versions/default/lib/python/
ENV ANSIBLE_CONFIG=/root/ansible_sakuli/ansible.cfg
RUN /omd/versions/default/bin/ansible-playbook -i localhost, /root/ansible_sakuli/playbook.yml -c local -e ANSIBLE_DROPIN_ROLE=$ANSIBLE_DROPIN_ROLE -vv

WORKDIR /root
