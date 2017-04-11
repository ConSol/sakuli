### build and start
* Centos7: 
  `docker build -t local/centos-xfce-vnc -f  Dockerfile.sakuli.centos.xfc . && docker run -d -p 5911:5901 toschneck/centos-xfce-vnc`
* Ubuntu:
  `docker build -t local/ubuntu-xfce-vnc -f Dockerfile.sakuli.ubuntu.xfce . && docker run -d -p 5912:5901 toschneck/ubuntu-xfce-vnc`

### update /etc/hosts 

To prevent gearman/mysql connections to run in timeouts: 

    docker run --add-host vbsakulidemo:192.168.122.35 ...

This updates /etc/hosts with 

    192.168.122.35  vbsakulidemo
