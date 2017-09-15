# This file is used to manage local images
# depending of the current dir and branch.
# Branch 'master' leads to no tag (=latest),
# others to "local/[dirname]:[branchname]

# run 'make echo' to show the image name you're working on.

build:
	make -f Makefile.omd-labs-centos-pnp4nagios-sakuli build
	make -f Makefile.omd-labs-centos-grafana-sakuli build
	make -f Makefile.omd-labs-debian-pnp4nagios-sakuli build
	make -f Makefile.omd-labs-debian-grafana-sakuli build
	make -f Makefile.omd-labs-ubuntu-pnp4nagios-sakuli build
	make -f Makefile.omd-labs-ubuntu-grafana-sakuli build
