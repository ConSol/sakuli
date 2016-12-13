# This file is used to manage local images
# depending of the current dir and branch.
# Branch 'master' leads to no tag (=latest),
# others to "local/[dirname]:[branchname]

# run 'make echo' to show the image name you're working on.

REPO = local/$(shell basename `pwd`)
TAG  = $(shell git rev-parse --abbrev-ref HEAD|sed 's/master/latest/')

IMAGE=$(REPO):$(TAG)


.PHONY: build start echo bash

build:
	docker build --build-arg BRANCH=$(TAG) -t $(IMAGE) .
	@docker images | grep '$(REPO)'
start:
	docker run -p 8443:443 -d $(IMAGE)
	@docker images | grep '$(REPO)'
echo:
	@echo $(IMAGE)
bash:
	docker run --rm -p 8443:443 -it $(IMAGE) /bin/bash
