## Timesheet-devops
`Timesheet-devops` is an Academic project allowing us to automatically execute tasks as jobs through a jenkins pipeline using a spring boot project with some other tools.
## Quick Start
 1. Setup maven
 1. Install sonar
 1. Install nexus
 1. Install jenkins
 1. Install docker
 1. Run sonar, nexus and jenkins servers
 1. Verify for JUnit test the src\test\java\tn\esprit\spring\service\UserServiceImplTest.java file tests and make sure they do not produce failure or error

### Dockerfile

If you want to wawork  with files from local project you can use this dockerfile settings

```sh
FROM openjdk:8-jdk-alpine
EXPOSE 8082
ADD target/timesheet-devops-1.0.jar timesheet-devops-1.0.jar
ENTRYPOINT ["java","-jar","/timesheet-devops-1.0.jar"]

```
If you want to work with jar created in remote repository by nexus you will need to get your host  ip address 
in order to access that address through the container, all you have to do is use `host.docker.internal`.

```sh
FROM openjdk:8-jdk-alpine
EXPOSE 8082
ADD http://host.docker.internal:8081/repository/maven-releases/tn/esprit/spring/timesheet-devops/1.0/timesheet-devops-1.0.jar timesheet-devops-1.0.jar
ENTRYPOINT ["java","-jar","/timesheet-devops-1.0.jar"]

```

### Jenkins Pipeline
-Don't forget to setup your docker hub credentials and create a new repository with tag you can visit this URI in jenkins NShttp://JENKI_IP:JENKINS_PORT/credentials/store/system/domain/_/newCredentials

```sh
pipeline {
  environment {
    registry = 'lqss/jenkins'
    registryCredential = 'dockerHub'
    dockerImage = ''
  }
  agent any
  stages {
    stage('Mail Notification') {
      steps {
        echo 'Sending Mail'
        mail bcc: '',
        body: 'Jenkins Build Started',
        cc: '',
        from: '',
        replyTo: '',
        subject: 'Jenkins Job',
        to: 'examplemail@gmail.com'
      }
    }
    stage('GIT Clone') {
      steps {
        echo 'Getting Project from Git'
        git 'https://github.com/LQss11/devops-pipeline.git'
      }
    }
    stage('MVN CLEAN') {
      steps {
        echo 'Maven Clean'
        bat 'mvn clean'
      }
    }
    stage('MVN TEST JUNIT') {
      steps {
        echo 'Maven Test JUnit'
        bat 'mvn test'
      }
    }
    stage('MVN TEST SONAR') {
      steps {
        echo 'Sonar Test Code Quality'
        bat 'mvn sonar:sonar'
      }
    }
    stage('MVN PACKAGE') {
      steps {
        echo 'Maven Packaging'
        bat 'mvn package -Dmaven.test.skip=true'
      }
    }
    stage('NEXUS') {
      steps {
        echo 'Nexus Packaging'
        bat 'mvn clean package -Dmaven.test.skip=true deploy:deploy-file -DgroupId=tn.esprit.spring -DartifactId=timesheet-devops -Dversion=1.0 -DgeneratePom=true -Dpackaging=jar  -DrepositoryId=deploymentRepo -Durl=http://localhost:8081/repository/maven-releases/ -Dfile=target/timesheet-devops-1.0.jar'
      }
    }
    stage('Building our image') {
      steps {
        script {
          dockerImage = docker.build registry + ":$BUILD_NUMBER"
        }
      }
    }
    stage('Deploy image to Docker Hub') {
      steps {
        script {
          docker.withRegistry('', registryCredential) {
            dockerImage.push()
          }
        }
      }
    }
    stage('Cleaning up Docker Image') {
      steps {
        bat "docker rmi $registry:$BUILD_NUMBER"
      }
    }
  }
      post {
        success {
        echo 'whole pipeline successful'
        mail bcc: '',
        body: "Project with name ${env.JOB_NAME}, with Build Number: ${env.BUILD_NUMBER}, was built successfully. to check the build result go to build URL: ${env.BUILD_URL}",
        cc: '',
        from: '',
        replyTo: '',
        subject: 'Build success',
        to: 'examplemail@gmail.com'
        }
        failure {
        echo 'pipeline failed, at least one step failed'
        mail bcc: '',
        body: "there was an error in  ${env.JOB_NAME}, with Build Number: ${env.BUILD_NUMBER} to check the error go to build URL: ${env.BUILD_URL}",
        cc: '',
        from: '',
        replyTo: '',
        subject: 'Build Failure',
        to: 'examplemail@gmail.com'
        }
      }
}
```

# Automatic Jenkins Pipeline test

## Jenkins Setup
Create A new jenkins pipeline.
In `Build Triggers` check the `GitHub hook trigger for GITScm polling` box.
In `Pipeline -> Definition` select `Pipeline script from SCM ` then use these parameters:
  1. `Repository URL`:https://github.com/LQss11/devops-pipeline.git
  1. `Branch Specifier (blank for 'any')`:*/master
  1. `Script Path`:Jenkinsfile
## Ngrok Setup 
Working with github webhooks wouldn't work if your jenkins is not hosted on a server connected to the internet (localhost will not be allowed).
In order to solve that all you have to do is work with `Ngrok` --> [Download Link](https://ngrok.com/download).
To set it up simply type this cmd after running ngrok.
Let's say jenkins is running on port `8083`.
 
```sh
ngrok http 8083
```
now ngrok will generate a http and https links for jenkins server that will be available to use with github webhooks.
our link would look something like this xxxx-xxx-xxx-xxx-xxx.ngrok.io
## Github Setup
  1. Select the repository you want to work with.
  1. Go to `Settings`.
  1. In options select `Webhooks` the create a new webhook (link must look like this https://REPOSITORY_URL/settings/hooks/new).
  1. `Payload URL`: http://xxxx-xxx-xxx-xxx-xxx.ngrok.io/github-webhook/ 
  1. `Content type`: application/json
  1. `Which events would you like to trigger this webhook?`:Just the push event. 

# Mailing
In order to use mailing service through your pipeline you can use the jenkins `mailer` plugin then all you have to do is set up the mailing notification configuration as the following details:
  1. Go to Jenkins configuration page -> `http://JENKINS_URL_AND_PORT/configure`
  1. Go all the way down to `E-mail Notification`
  1. `SMTP server`: smtp.gmail.com
  1. Click advanced
  1. Check `Use SMTP Authentication`and `Use SSL`
  1. `User Name`: sender email
  1. `Password`: email password
  1. `SMTP Port`: 465
  1. Finally ckeck Test configuration by sending test e-mail, type an email you want to test the service on then click `test configuration` -> you will recieve a mail once you click it and that means the service works properly for you and don't forget to save your settings.
### Mailing Issues
Configuring the email service can be challenging sometimes once you encounter some annoying issues as happened for me
```sh
sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```
```sh
javax.mail.MessagingException: Could not connect to SMTP host: smtp.gmail.com, port: 465;
```
```sh
javax.mail.AuthenticationFailedException: 535-5.7.8 Username and Password not accepted
```
If you encountred one of these issues it is most likely that you havent allowed your email sender to send an email through thais service so all you have to do is login to you sender email then `Mail settings` -> `Security` -> `Enable less secure app access` , also make sure you have entered the right email and password if error still persisting then it must be something related to your firewall stopping you from sending SMTP requests using jenkins, so to solve this temporary all I had to do is disable my `Avast antivirus` for an hour and test the configuration and it worked like charm.

If you have any issue feel free to post you issue in the `Issues section` and I would be so happy to help you

## Jenkins pipeline final result 
<p align="center">
  <img src="https://raw.githubusercontent.com/LQss11/devops-pipeline/master/images/Jenkins_Results.png" title="Jenkins pipeline">
</p>
