FROM jenkins/jenkins:lts

ARG MAVEN_V

# Use root user
USER root

# install wget
RUN apt-get update && apt-get install -y wget

# Install Initial plugins --skip-failed-plugins 
RUN jenkins-plugin-cli --plugins authorize-project \
    ace-editor ant antisamy-markup-formatter pam-auth \
    apache-httpcomponents-client-4-api bootstrap4-api \ 
    bootstrap5-api bouncycastle-api branch-api okhttp-api \ 
    build-timeout caffeine-api checks-api momentjs \ 
    cloudbees-folder command-launcher credentials-binding \ 
    credentials display-url-api durable-task echarts-api \ 
    email-ext font-awesome-api git-client git-server git \
    github-api github-branch-source github gradle handlebars \
    jackson2-api javax-activation-api javax-mail-api jaxb \ 
    jdk-tool jjwt-api jquery3-api jsch junit ldap \
    lockable-resources mailer matrix-auth matrix-project \
    pipeline-build-step pipeline-github-lib \
    pipeline-graph-analysis pipeline-input-step \
    pipeline-model-api pipeline-model-definition \
    pipeline-model-extensions pipeline-rest-api \ 
    pipeline-stage-step pipeline-stage-tags-metadata \ 
    pipeline-stage-view plain-credentials plugin-util-api \
    popper-api popper2-api resource-disposer scm-api \ 
    pipeline-milestone-step script-security snakeyaml-api \
    ssh-credentials ssh-slaves sshd structs timestamper \
    token-macro trilead-api workflow-aggregator workflow-api \
    workflow-basic-steps workflow-cps-global-lib workflow-cps \ 
    workflow-durable-task-step workflow-job workflow-multibranch \
    workflow-scm-step workflow-step-api workflow-support ws-cleanup
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

COPY /jenkins/dockerhub-cred.groovy /usr/share/jenkins/ref/init.groovy.d/


COPY /jenkins/default-user.groovy /usr/share/jenkins/ref/init.groovy.d/
# Create default admin user
#ENV JENKINS_OPTS--argumentsRealm.roles.user=$JENKINS_USER --argumentsRealm.passwd.admin=$JENKINS_PASS --argumentsRealm.roles.admin=admin


# remove download archive files
RUN apt-get clean

# volume for Jenkins settings
VOLUME /var/jenkins_home
