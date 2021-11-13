## Timesheet-devops
`Timesheet-devops` is an Academic project allowing us to automatically execute tasks as jobs through a jenkins pipeline using a spring boot project with some other tools.
## Quick Start
 1. Install maven
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
in my case it's `Ethernet adapter vEthernet (Default Switch):`

```sh
FROM openjdk:8-jdk-alpine
EXPOSE 8082
ADD http://172.19.80.1:8081/repository/maven-releases/tn/esprit/spring/timesheet-devops/1.0/timesheet-devops-1.0.jar timesheet-devops-1.0.jar
ENTRYPOINT ["java","-jar","/timesheet-devops-1.0.jar"]

```

### Jenkins Pipeline
-Don't forget to setup your docker hub credentials and create a new repository with tag you can visit this URI in jenkins http://JENKINS_IP:JENKINS_PORT/credentials/store/system/domain/_/newCredentials
```sh
pipeline {
  environment {
    registry = "lqss/jenkins"
    registryCredential = 'dockerHub'
    dockerImage = ''
  }    
  agent any
  stages {
    stage('GIT') {
      steps {
        echo "Getting Project from Git";
        git 'https://github.com/LQss11/devops-pipeline.git';
      }
    }
    stage('MVN CLEAN') {
      steps {
        echo "Maven Clean";
        bat 'mvn clean';
      }
    }
    stage('MVN TEST JUNIT') {
      steps {
        echo "Maven Test JUnit";
        bat 'mvn test';
      }
    }
    stage('MVN PACKAGE') {
      steps {
        echo "Maven Packaging";
        bat 'mvn package -Dmaven.test.skip=true';
      }
    }
    stage('MVN TEST SONAR') {
      steps {
        echo "Sonar Test Code Quality";
        bat 'mvn sonar:sonar';
      }
    }    
    stage('NEXUS') {
      steps {
        echo "Nexus Packaging";
        bat 'mvn clean package -Dmaven.test.skip=true deploy:deploy-file -DgroupId=tn.esprit.spring -DartifactId=timesheet-devops -Dversion=1.0 -DgeneratePom=true -Dpackaging=jar  -DrepositoryId=deploymentRepo -Durl=http://localhost:8081/repository/maven-releases/ -Dfile=target/timesheet-devops-1.0.jar';
      }
    }    
    stage('Building our image') {
      steps {
        script {
          dockerImage = docker.build registry + ":$BUILD_NUMBER"
        }
      }
    }
    stage('Deploy our image') {
      steps {
        script {
          docker.withRegistry('', registryCredential) {
            dockerImage.push()
          }
        }
      }
    }
    stage('Cleaning up') {
      steps {
        bat "docker rmi $registry:$BUILD_NUMBER"
      }
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


