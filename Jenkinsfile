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
        to: 'affessalem@hotmail.fr'
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
    stage('MVN PACKAGE') {
      steps {
        echo 'Maven Packaging'
        bat 'mvn package -Dmaven.test.skip=true'
      }
    }
    stage('MVN TEST SONAR') {
      steps {
        echo 'Sonar Test Code Quality'
        bat 'mvn sonar:sonar'
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
        }
        failure {
      echo 'pipeline failed, at least one step failed'
        }
      }
}
