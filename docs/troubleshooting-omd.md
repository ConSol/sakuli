# Troubleshooting Nagios/OMD

## MySQL installation
### Apparmor prevention 

**Error:** *mysql_install_db fails: 

	141021 16:40:03 [Warning] Can't create test file /omd/sites/sakuli/var/mysql/omd2.lower-test
	ERROR: 1005  Can't create table 'db' (errno: 13)

**Error:** MySQL startup fails: 

	OMD[sakuli]:~$ omd start
	Starting gearmand...OK
	Starting MySQL... ..........ERROR.
	Starting rrdcached...OK

**Solution:**
	
Check `/var/log/syslog` or `/var/log/messages` for apparmor messages:

	…
	Oct 21 17:08:21 omd2 kernel: [116300.215520] type=1400 audit(1413904101.323:27): apparmor="DENIED" operation="open" profile="/usr/sbin/mysqld" name="/opt/omd/sites/sakuli/.my.cnf" pid=13136 comm="mysqld" requested_mask="r" denied_mask="r" fsuid=999 ouid=999
	…

Apparmor has prevented you from using a non-default config file for MySQL. If you know how to create a apparmor profile for MySQL on OMD, let us know :-) 

The quick solution is to completely disable apparmor. Check if unloading apparmor profiles solves the problem: 

	root@omd:~# service apparmor teardown
	 * Unloading AppArmor profiles

If so, execute the following command to uninstall apparmor: 

	root@omd2:~# apt-get remove apparmor


## Performance Data

### No CPU/Mem graphs 

FIXME

  <NAGIOS_E2ECPUHOST>win7sakuli</NAGIOS_E2ECPUHOST>
  <NAGIOS_E2ECPUSVC>CPU_Usage</NAGIOS_E2ECPUSVC>
  <NAGIOS_E2EMEMHOST>win7sakuli</NAGIOS_E2EMEMHOST>
  <NAGIOS_E2EMEMSVC>Mem_Usage</NAGIOS_E2EMEMSVC>