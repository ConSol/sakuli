# This file is used to manage local images
# depending of the current dir and branch.
# Branch 'master' leads to no tag (=latest),
# others to "local/[dirname]:[branchname]

# run 'make echo' to show the image name you're working on.

DIRNAME = $(shell basename `pwd`)
LOCAL_REPO = local/$(DIRNAME)
REMOTE_REPO = consol/$(DIRNAME)
TAG  = $(shell git rev-parse --abbrev-ref HEAD|grep -v master)

.PHONY: build bash start stop

start:
	docker run -p 8443:443 -d $(REMOTE_REPO):$(TAG)
	docker images | grep '$(REMOTE_REPO)'
echo:
	echo $(REMOTE_REPO):$(TAG)
bash:
	docker run --rm -p 8443:443 -it --name omd-labs $(REMOTE_REPO):$(TAG) /bin/bash

build-local:
	docker build -t $(LOCAL_REPO):$(TAG) .
	docker images | grep '$(LOCAL_REPO)'
start-local:
	docker run -p 8443:443 -d $(LOCAL_REPO):$(TAG)
	docker images | grep '$(LOCAL_REPO)'
echo-local:
	echo $(LOCAL_REPO):$(TAG)
bash-local:
	docker run --rm -p 8443:443 -it --name omd-labs $(LOCAL_REPO):$(TAG) /bin/bash
