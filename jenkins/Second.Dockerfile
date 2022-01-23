FROM jenkins/jenkins:lts

ARG MAVEN_V

USER root

# install wget
RUN apt-get update
RUN apt-get install -y wget

# Install docker pipeline plugin
RUN jenkins-plugin-cli --plugins docker-workflow:1.27
# Install plugins for initial admin user setup script (blueocean:1.25.2 maven-plugin:3.16)
RUN jenkins-plugin-cli --plugins matrix-auth:3.0 
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
ENV JENKINS_USER admin
ENV JENKINS_PASS admin

# Skip both install and upgrade wizards -- this will not enable security options 
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

#COPY /jenkins/default-user.groovy /usr/share/jenkins/ref/init.groovy.d/
# Create default admin user
ENV JENKINS_OPTS--argumentsRealm.roles.user=$JENKINS_USER --argumentsRealm.passwd.admin=$JENKINS_PASS --argumentsRealm.roles.admin=admin
# remove download archive files
RUN apt-get clean

# volume for Jenkins settings
VOLUME /var/jenkins_home
