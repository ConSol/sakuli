FROM consol/omd-labs-ubuntu:nightly

MAINTAINER Simon Meggle "simon.meggle@consol.de"
ENV REFRESHED_AT 2018-06-27

ENV TZ Europe/Berlin

# Add the Ansible provisioning role
ADD ansible_provision /root/ansible_provision

RUN /omd/sites/$SITENAME/bin/ansible-playbook -i localhost, /root/ansible_provision/playbook.yml -c local -e GRAPHER=grafana -e SITENAME=$SITENAME
