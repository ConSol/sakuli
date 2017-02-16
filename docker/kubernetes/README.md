# Kubernetes usage of the Sakuli E2E testing and monitoring Docker images

First go the folder of the Kubernetes conf:

    cd <project-path>/docker/kubernetes

## Start sakuli test execution pod

    kubectl create -f kubernetes.sakuli.example.pod.run.yaml

This creates the following components in the Kubernetes cluster:
* new Namespace `sakuli`
* 1 Pod with the sakuli container running
* 1 Service to make the container ports `6901` and `5901` accessible 

Take a look to the Kubernetes dashboard

    http://<kubernets-cluster-ip>:30000/?namespace=sakuli

Now you can connect (during the time of the test execution) to container and take look into UI of the test execution. This will be done through the defined external port `32001` of the service `test-sakuli-kub`

    http://<kubernets-cluster-ip>:32001/vnc_auto.html?password=sakuli&view_only=true


## Delete sakuli test execution pod

    kubectl delete -f kubernetes.sakuli.example.pod.run.yaml