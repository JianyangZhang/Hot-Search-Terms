### Nodes Hadoop Cluster

#####1. pull docker image

```
sudo docker pull joway/hadoop-cluster

```

#####2. clone github repository

```
git clone https://github.com/joway/hadoop-cluster-docker
```

#####3. create hadoop network

```
sudo docker network create --driver=bridge hadoop
```

#####4. start container

```
cd hadoop-cluster-docker
sudo ./start-container.sh
```

**output:**

```
start hadoop-master container...
start hadoop-slave1 container...
start hadoop-slave2 container...
root@hadoop-master:~# 
```
- start 3 containers with 1 master and 2 slaves
- you will get into the /root directory of hadoop-master container

#####5. start hadoop

```
./start-hadoop.sh
```

#####6. run wordcount

```
./run-wordcount.sh
```

**output**

```
input file1.txt:
Hello Hadoop

input file2.txt:
Hello Docker

wordcount output:
Docker    1
Hadoop    1
Hello    2
```

#### 7. sync src

本地目录: ~/src/

hadoop master 目录 : /root/src/

将代码copy到本地目录 ~/src/ 中, 但凡在该目录下的任何操作都会自动实时映射到 hadoop master 容器中的 /root/src/ 中

之后只要在本地~/src/中修改编辑代码, 在容器内执行命令即可

#### 8. 

相关便捷脚本:

1. ./build_image.sh 构建你的新镜像 ( 修改 Dockerfile 后 )
2. ./enter_master_bash.sh  进入hadoop_master 容器
3. ./delete_all_container.sh  清空所有docker容器
