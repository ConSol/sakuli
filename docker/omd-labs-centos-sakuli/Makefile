# This file is used to manage local images
# depending of the current dir and branch.
# Branch 'master' leads to no tag (=latest),
# others to "local/[dirname]:[branchname]

# run 'make echo' to show the image name you're working on.

REPO = local/$(shell basename `pwd`)
TAG  = $(shell git rev-parse --abbrev-ref HEAD|sed 's/master/latest/')
BUILDARGS = --build-arg SOURCE_BRANCH=$(TAG)

IMAGE=$(REPO):$(TAG)

ifdef SOURCE_BRANCH
	BUILDARGS = --build-arg SOURCE_BRANCH=$(SOURCE_BRANCH)
endif

ifndef SITENAME
  SITENAME=demo
endif

.PHONY: build start echo bash

build:
	# use the local base image with tag = branchname
	sed 's@^\(FROM\) consol\(/omd-labs-.*\)@\1 local\2:$(TAG)@' Dockerfile > Dockerfile.local
	docker build -f Dockerfile.local $(BUILDARGS) -t $(IMAGE) .
	@rm Dockerfile.local
	@echo "Successfully built" $(IMAGE)
start:
	docker run -p 8443:443 -d $(IMAGE)
startvol:
	docker run -d -p 8443:443             \
	-v $(shell pwd)/site/local:/omd/sites/$(SITENAME)/local \
	-v $(shell pwd)/site/etc:/omd/sites/$(SITENAME)/etc     \
	-v $(shell pwd)/site/var:/omd/sites/$(SITENAME)/var     \
	$(IMAGE)
echo:
	@echo $(IMAGE)
bash:
	docker run --rm -p 8443:443 -it $(IMAGE) /bin/bash
bashvol:
	docker run --rm -p 8443:443 -it \
	-v $(shell pwd)/site/local:/omd/sites/$(SITENAME)/local \
	-v $(shell pwd)/site/etc:/omd/sites/$(SITENAME)/etc     \
	-v $(shell pwd)/site/var:/omd/sites/$(SITENAME)/var     \
	$(IMAGE) /bin/bash
