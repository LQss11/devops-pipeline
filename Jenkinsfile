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
        git branch: "devops-cicd", url: 'https://github.com/LQss11/devops-pipeline.git'
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
