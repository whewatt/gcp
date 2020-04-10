# This is a comment
FROM centos
MAINTAINER Wes Hewatt <whewatt@pivotal.io>
RUN yum -y install java
RUN yum -y install wget
RUN wget https://repo.spring.io/libs-release-local/org/springframework/xd/spring-xd/1.1.2.RELEASE/spring-xd-1.1.2.RELEASE-1.noarch.rpm
RUN rpm -ivh spring-xd-1.1.2.RELEASE-1.noarch.rpm
RUN /opt/pivotal/spring-xd-1.1.2.RELEASE/xd/bin/xd-singlenode
