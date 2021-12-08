pipeline {
  environment {
    registry = 'lqss/jenkins'
    registryCredential = 'dockerHub'
    dockerImage = ''
  }    
  agent any
  stages {
    stage('Git') {
      steps {
        echo 'Cloning'
        git branch: "spring-for-jenkins-with-docker", url: 'https://github.com/LQss11/devops-pipeline.git'
      }
    }
    stage('MVN CLEAN') {
      steps {
        echo 'Maven Clean'
        sh 'mvn clean'
      }
    }
    stage('MVN TEST JUNIT') {
      steps {
        echo 'Maven Test JUnit'
        sh 'mvn test'
      }
    }
    stage('MVN TEST SONAR') {
      steps {
        echo 'Sonar Test Code Quality'
        sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000'
      }
    }
    stage('MVN PACKAGE') {
      steps {
        echo 'Maven Packaging'
        sh 'mvn package -Dmaven.test.skip=true'
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
        sh "docker rmi $registry:$BUILD_NUMBER"
      }
    }    
  }
}
