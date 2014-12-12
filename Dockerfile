FROM ubuntu:14.10
MAINTAINER David Gageot <david@gageot.net>

RUN apt-get update -qq && apt-get install -yqq \
  curl \
  phantomjs

# Install java
#
RUN (curl -s -k -L -C - -b "oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u25-b17/jdk-8u25-linux-x64.tar.gz | tar xfz -)

ENV JAVA_HOME /jdk1.8.0_25
ENV PATH $PATH:$JAVA_HOME/bin

# Install maven
#
RUN curl -s http://apache.crihan.fr/dist/maven/maven-3/3.1.1/binaries/apache-maven-3.1.1-bin.tar.gz | tar xzf - -C /

ENV MAVEN_HOME /apache-maven-3.1.1
ENV PATH $PATH:$MAVEN_HOME/bin

# Project code
#
WORKDIR /home/simplelenium

# Warmup maven by building an old version that we don't change often
#
ADD docker/old_version.tgz /home/simplelenium
RUN mvn clean install -DskipTests && rm -Rf /home/simplelenium

# Add all sources from docker context
#
ADD . /home/simplelenium
RUN mvn clean install
