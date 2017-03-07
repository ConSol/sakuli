# This file is used to manage local images
# depending of the current dir and branch.
# Branch 'master' leads to no tag (=latest),
# others to "local/[dirname]:[branchname]

# run 'make echo' to show the image name you're working on.

REPO = local/$(shell basename `pwd`)
TAG  = $(shell git rev-parse --abbrev-ref HEAD|sed 's/master/latest/')
BUILDARG1= SOURCE_BRANCH=$(TAG)
BUILDARG2= NEW_SITENAME=demo

IMAGE=$(REPO):$(TAG)

ifdef SOURCE_BRANCH
	BUILDARG1= SOURCE_BRANCH=$(SOURCE_BRANCH)
endif

ifdef NEW_SITENAME
	BUILDARG2 = NEW_SITENAME=$(NEW_SITENAME)
endif

BUILDARGS = --build-arg $(BUILDARG1) --build-arg $(BUILDARG2)
echo "buildargs: $(BUILDARGS)"

.PHONY: build start echo bash

build:
	# use the local base image with tag = branchname
	sed 's@^\(FROM\) consol\(/omd-labs-.*\)@\1 local\2:$(TAG)@' Dockerfile > Dockerfile.local
	docker build -f Dockerfile.local $(BUILDARGS) -t $(IMAGE) .
	@rm Dockerfile.local
	@echo "Successfully built" $(IMAGE)
start:
	docker run --name omd -p 8443:443 -d $(IMAGE)
startvol:
	docker run --name omd -d -p 8443:443             \
	-v $(shell pwd)/site/local:/omd/sites/$(SITENAME)/local \
	-v $(shell pwd)/site/etc:/omd/sites/$(SITENAME)/etc     \
	-v $(shell pwd)/site/var:/omd/sites/$(SITENAME)/var     \
	$(IMAGE)
echo:
	@echo $(IMAGE)
bash:
	docker run --name omd --rm -p 8443:443 -it $(IMAGE) /bin/bash
bashvol:
	docker run --name omd --rm -p 8443:443 -it \
	-v $(shell pwd)/site/local:/omd/sites/$(SITENAME)/local \
	-v $(shell pwd)/site/etc:/omd/sites/$(SITENAME)/etc     \
	-v $(shell pwd)/site/var:/omd/sites/$(SITENAME)/var     \
	$(IMAGE) /bin/bash
