# Sakuli & OMD-Labs

Create Docker images with OMD Labs ([https://labs.consol.de/OMD/](https://labs.consol.de/OMD/)) and Sakuli extensions.

## Preparation

    pip install ansible-container

## Step 1: Update source files

    make updatefiles

Synchronize all needed files from the Sakuli project into the ansible folder.

## Step 2: Build images

    # build images for centos/ubuntu/debian
    make build
    # build single image
    make build-ubuntu
    make build-debian
    make build-centos

## Step 3: Push images

    docker push consol/sakuli-omd-labs-ubuntu
    docker push consol/sakuli-omd-labs-debian
    docker push consol/sakuli-omd-labs-centos
