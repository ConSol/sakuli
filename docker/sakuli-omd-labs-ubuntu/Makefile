# This file is used to manage local images
# depending of the current dir and branch.
# Branch 'master' leads to no tag (=latest),
# others to "local/[dirname]:[branchname]

# run 'make echo' to show the image name you're working on.

REPO = local/$(shell basename `pwd`)
TAG  = $(shell git rev-parse --abbrev-ref HEAD|grep -v master)

IMAGE=$(REPO):$(TAG)


.PHONY: build bash start stop

build:
	docker build -t $(IMAGE) .
	docker images | grep '$(REPO)'
start:
	docker run -p 8080:80 -d $(IMAGE)
	docker images | grep '$(REPO)'
echo:
	echo $(IMAGE)
bash:
	docker run --rm -p 8080:80 -it --name omd-labs $(IMAGE) /bin/bash
