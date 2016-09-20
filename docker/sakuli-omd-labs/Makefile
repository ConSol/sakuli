DIRNAME = $(shell basename `pwd`)
LOCAL_REPO = local/$(DIRNAME)
REMOTE_REPO = consol/$(DIRNAME)
TAG  = $(shell git rev-parse --abbrev-ref HEAD|grep -v master)
PROJECT_ROOT = ../..
SAKULI_SETUP = $(PROJECT_ROOT)/src/common/src/main/resources/org/sakuli/common/setup
ANSIBLE_FILES = ansible/sakuli/files

ifeq (, $(shell which ansible-container))
 $(error "No ansible-container in $(PATH), consider installing it using pip/virtualenv?")
endif

.PHONY: build updatefiles

# update the ansible dir with the newest files from the current branch
updatefiles:
	mkdir -p $(ANSIBLE_FILES)/etc/{cron.d,check_mysql_health,mod-gearman,apache/conf.d,nagios/conf.d,pnp4nagios/templates}
	mkdir -p $(ANSIBLE_FILES)/etc/thruk/{ssi,thruk_local.d}
	mkdir -p $(ANSIBLE_FILES)/local/bin
	mkdir -p $(ANSIBLE_FILES)/local/lib/nagios/plugins

	# Update all files for gearman-proxy
	cp $(SAKULI_SETUP)/nagios/gearman_proxy/sakuli_gearman_proxy.cron $(ANSIBLE_FILES)/etc/cron.d/
	cp $(SAKULI_SETUP)/nagios/gearman_proxy/sakuli_gearman_proxy.cfg $(ANSIBLE_FILES)/etc/mod-gearman/
	cp $(SAKULI_SETUP)/nagios/gearman_proxy/sakuli_gearman_proxy.pl $(ANSIBLE_FILES)/local/bin/

	# Update all files for screenshot history
	cp $(SAKULI_SETUP)/nagios/screenshot_history/sakuli_screenshots.conf $(ANSIBLE_FILES)/etc/apache/conf.d/
	cp $(SAKULI_SETUP)/nagios/screenshot_history/sakuli_screenshot_eventhandler.cfg $(ANSIBLE_FILES)/etc/nagios/conf.d/
	cp $(SAKULI_SETUP)/nagios/screenshot_history/sakuli_screenshot_eventhandler.sh $(ANSIBLE_FILES)/local/lib/nagios/plugins/
	cp $(SAKULI_SETUP)/nagios/screenshot_history/*.ssi $(ANSIBLE_FILES)/etc/thruk/ssi/
	cp $(SAKULI_SETUP)/nagios/screenshot_history/sakuli_action_menu.conf $(ANSIBLE_FILES)/etc/thruk/thruk_local.d/

	# Update Sakuli DB check
	cp $(SAKULI_SETUP)/nagios/CheckMySQLHealthSakuli.pm $(ANSIBLE_FILES)/etc/check_mysql_health/

	# Update PNP4Nagios Sakuli template
	cp $(SAKULI_SETUP)/nagios/check_sakuli.php $(ANSIBLE_FILES)/etc/pnp4nagios/templates/

  # Nagios generic objects
	cp $(SAKULI_SETUP)/nagios/sakuli_nagios_objects.cfg $(ANSIBLE_FILES)/etc/nagios/conf.d/


build: 	build-ubuntu build-centos build-debian

build-ubuntu:
	ln -f -s container_ubuntu.yml ansible/container.yml
	ansible-container build -- -vv
	docker tag $(DIRNAME)-ubuntu:latest $(REMOTE_REPO)-ubuntu:$(TAG)
	docker tag $(DIRNAME)-ubuntu:latest $(REMOTE_REPO)-ubuntu

build-centos:
	ln -f -s container_centos.yml ansible/container.yml
	ansible-container build -- -vv
	docker tag $(DIRNAME)-centos:latest $(REMOTE_REPO)-centos:$(TAG)
	docker tag $(DIRNAME)-centos:latest $(REMOTE_REPO)-centos

build-debian:
	ln -f -s container_debian.yml ansible/container.yml
	ansible-container build -- -vv
	docker tag $(DIRNAME)-debian:latest $(REMOTE_REPO)-debian:$(TAG)
	docker tag $(DIRNAME)-debian:latest $(REMOTE_REPO)-debian
