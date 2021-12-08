FROM jenkins/jenkins:lts

ARG MAVEN_VERSION="3.5.4"

USER root

# install wget
RUN apt-get update
RUN apt-get install -y wget


# install jenkins plugins
#RUN /usr/local/bin/install-plugins.sh github-pullrequest:plugin-version:https://plugins.jenkins.io/github-pullrequest/
#RUN /usr/local/bin/install-plugins.sh plugin-name:0.3.0:hpi-url
# jenkins-plugin-cli --plugin-file /your/path/to/plugins.txt --plugins delivery-pipeline-plugin:1.3.2 deployit-plugin
# jenkins-plugin-cli --plugins pluginname
# get maven 3.2.2
RUN wget --no-verbose -O /tmp/apache-maven-${MAVEN_VERSION}-bin.tar.gz http://apache.cs.utah.edu/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz

# verify checksum
# RUN echo "35c39251d2af99b6624d40d801f6ff02 /tmp/apache-maven-3.4.0-bin.tar.gz" | md5sum -c

# install maven
RUN tar xzf /tmp/apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt/
RUN ln -s /opt/apache-maven-${MAVEN_VERSION} /opt/maven
RUN ln -s /opt/maven/bin/mvn /usr/local/bin
RUN rm -f /tmp/apache-maven-${MAVEN_VERSION}-bin.tar.gz
ENV MAVEN_HOME /opt/maven

RUN chown -R jenkins:jenkins /opt/maven

# install git (MH: Git should be preinstalled in the original Jenkins docker image prep)
# RUN apt-get install -y git

# remove download archive files
RUN apt-get clean

#USER jenkins