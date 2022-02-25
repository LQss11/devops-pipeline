FROM jenkins/jenkins:lts

ARG MAVEN_V

# Use root user
USER root

# install wget
RUN apt-get update && apt-get install -y wget

# Install init plugins
RUN jenkins-plugin-cli --plugins docker-workflow cloudbees-folder timestamper \
ws-cleanup pipeline-stage-view pam-auth pipeline-github-lib \
matrix-auth ssh build-timeout github-branch-source  ssh-slaves \
email-ext antisamy-markup-formatter workflow-aggregator git ldap authorize-project

# get maven
RUN wget --no-verbose -O /tmp/apache-maven-$MAVEN_V-bin.tar.gz http://apache.cs.utah.edu/maven/maven-3/$MAVEN_V/binaries/apache-maven-$MAVEN_V-bin.tar.gz

# install maven
RUN tar xzf /tmp/apache-maven-$MAVEN_V-bin.tar.gz -C /opt/
RUN ln -s /opt/apache-maven-$MAVEN_V /opt/maven
RUN ln -s /opt/maven/bin/mvn /usr/local/bin
RUN rm -f /tmp/apache-maven-$MAVEN_V-bin.tar.gz

RUN chown -R jenkins:jenkins /opt/maven

# Setup maven home
ENV MAVEN_HOME /opt/maven

# Jenkins default admin user name and password 
ARG JENKINS_USER
ARG JENKINS_PASS
ENV JENKINS_USER $JENKINS_USER
ENV JENKINS_PASS $JENKINS_PASS

# Dockerfile credentials for the dockerhub-cred.groovy script
ARG DOCKER_USER
ARG DOCKER_PASS
ENV DOCKER_USER $DOCKER_USER
ENV DOCKER_PASS $DOCKER_PASS
# Setup default jenkins port for BUILD URL
ARG JENKINS_PORT
ENV JENKINS_PORT $JENKINS_PORT

# Skip both install and upgrade wizards -- this will not enable security options 
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

# Init script to create docker hub credentials
COPY /jenkins/dockerhub-cred.groovy /usr/share/jenkins/ref/init.groovy.d/
# Init script to create default admin user
COPY /jenkins/default-user.groovy /usr/share/jenkins/ref/init.groovy.d/
# Create default admin user
#ENV JENKINS_OPTS--argumentsRealm.roles.user=$JENKINS_USER --argumentsRealm.passwd.admin=$JENKINS_PASS --argumentsRealm.roles.admin=admin


# remove download archive files
RUN apt-get clean

# volume for Jenkins settings
VOLUME /var/jenkins_home
