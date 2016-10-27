# This file is used to manage local containers
# depending of the current dir and branch. 
# Branch 'master' leads to no tag (=latest), 
# others to "local/[dirname]:[branchname]

# run 'make echo' to show the image name you're working on.

REPO = local/$(shell basename `pwd`)
TAG  = $(shell git rev-parse --abbrev-ref HEAD|grep -v master)

IMAGE=$(REPO):$(TAG)

build: 
	docker build -t $(IMAGE) . 
	docker images | grep '$(REPO)'
run:
	docker run -p 5901:5901 -it -d $(IMAGE)
	docker images | grep '$(REPO)'
echo:
	echo $(IMAGE)
bash: 
	docker run --rm -p 5901:5901 -it $(IMAGE) /bin/bash
