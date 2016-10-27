FROM consol/omd-labs-ubuntu:latest
MAINTAINER The Sakuli team.  <sakuli@consol.de>

ENV REFRESHED_AT 2015-11-25

WORKDIR /omd/sites/demo/
ADD contrib/CheckMySQLHealthSakuli.pm /omd/sites/demo/etc/check_mysql_health/
ADD contrib/check_sakuli.php /omd/sites/demo/etc/pnp4nagios/templates/
ADD contrib/resource.cfg /omd/sites/demo/etc/nagios/ 
ADD contrib/sakuli_nagios_objects.cfg /omd/sites/demo/etc/nagios/conf.d/
ADD contrib/thruk_local.conf /omd/sites/demo/etc/thruk/

RUN sed -i 's|\(escape_html_tags=\)1|\10|' /omd/sites/demo/etc/nagios/cgi.cfg

# setup mod_gearman
RUN omd config demo set GEARMAND_PORT :4730
RUN omd config demo set GEARMAND on
RUN omd config demo set MOD_GEARMAN on
RUN omd config demo set GEARMAN_NEB on
RUN omd config demo set GEARMAN_WORKER off
RUN sed -i -e 's/\(services\|eventhandler\|hosts\|encryption\)=yes/\1=no/' -e 's/\(accept_clear_results\)=no/\1=yes/' /omd/sites/demo/etc/mod-gearman/server.cfg
