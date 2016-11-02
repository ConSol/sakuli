# This Dockerfile is used to build an sakuli image based on Ubuntu

FROM consol/omd-labs-centos
MAINTAINER Simon Meggle "simon.meggle@consol.de"
ENV REFRESHED_AT 2016-10-05

#ENV BRANCH=dev
#ENV GITHUB_URL https://github.com/ConSol/sakuli.git
#RUN git clone --depth 1 --branch $BRANCH --single-branch $GITHUB_URL /tmp/sakuli


ADD ansible /tmp/ansible

ENV OMD_DEMO /opt/omd/sites/demo
ENV PYTHONPATH=/omd/versions/default/lib/python/
ENV ANSIBLE_CONFIG=/root/ansible/ansible.cfg
# dropin-role dir must be mounted as volume into /root/ansible (=playbook_dir)
# e.g. -v $(pwd)/somewhere/on/host:/root/ansible/dropin_role
ENV ANSIBLE_DROPIN_ROLE=dropin_role
ENV ANSIBLE_VERBOSITY=

ENV REFRESHED 20160912
COPY ansible /root/ansible

WORKDIR /root
#CMD /root/ansible/playbook_starter.sh
