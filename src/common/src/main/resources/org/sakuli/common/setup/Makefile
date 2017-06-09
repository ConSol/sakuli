ifndef OMD_ROOT
$(error OMD_ROOT is not set. Are you a site user?)
endif

all: screenshot_history gearman_proxy grafana config mysql

screenshot_history:
	cp ./omd/local/lib/nagios/plugins/sakuli_screenshot_eventhandler.sh $(OMD_ROOT)/local/lib/nagios/plugins/
	cp ./omd/etc/nagios/conf.d/sakuli_screenshot_eventhandler.cfg $(OMD_ROOT)/etc/core/conf.d/
	cp ./omd/local/bin/sakuli_screenshot_cleanup.sh $(OMD_ROOT)/local/bin/
	cp ./omd/etc/cron.d/sakuli_screenshot_cleanup $(OMD_ROOT)/etc/cron.d/
	cp ./omd/etc/thruk/ssi/*.ssi $(OMD_ROOT)/etc/thruk/ssi/
	cp ./omd/etc/thruk/thruk_local.d/sakuli_action_menu.conf $(OMD_ROOT)/etc/thruk/thruk_local.d/
	cp ./omd/etc/apache/conf.d/sakuli_screenshots.conf $(OMD_ROOT)/etc/apache/conf.d/
	omd reload crontab
	omd reload apache
	omd reload core

gearman_proxy: gearman_proxy_install config

gearman_proxy_install:
	cp ./omd/etc/init.d/sakuli_gearman_proxy $(OMD_ROOT)/etc/init.d/
	chmod +x $(OMD_ROOT)/etc/init.d/sakuli_gearman_proxy
	cp ./omd/local/bin/sakuli_gearman_proxy.pl $(OMD_ROOT)/local/bin/
	cp ./omd/etc/mod-gearman/sakuli_gearman_proxy.cfg $(OMD_ROOT)/etc/mod-gearman/

config:
	mkdir -p $(OMD_ROOT)/local/share/nagios/htdocs/images/logos/
	cp ./omd/local/share/nagios/htdocs/images/logos/sakuli.png $(OMD_ROOT)/local/share/nagios/htdocs/images/logos/
	cp ./omd/etc/nagios/conf.d/sakuli_nagios_templates.cfg $(OMD_ROOT)/etc/core/conf.d/
	omd reload core

mysql: config
	mkdir -p $(OMD_ROOT)/etc/check_mysql_health/
	cp ./omd/etc/check_mysql_health/CheckMySQLHealthSakuli.pm $(OMD_ROOT)/etc/check_mysql_health/
	cp ./omd/etc/nagios/conf.d/sakuli_nagios_check_mysql.cfg $(OMD_ROOT)/etc/core/conf.d/
	omd stop
	omd config set MYSQL on
	omd start
	mysql < database/create_sakuli_database.sql
	omd reload core

grafana:
	patch -d $(OMD_ROOT) -p0 < ./omd/etc/init.d/sakuli_influxdb.patch
	omd restart influxdb
