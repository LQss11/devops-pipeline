# Devops-pipeline
`DevOps-pipeline` helps you set Up and automate a Continuous Integration & Delivery (CI/CD) process with Docker and Docker-compose.

# Quick Start
## Prerequisites 
This project uses a set of applications, but don't worry all of them are dockerized you will only need to have [Docker](https://docs.docker.com/engine/install/) and [Docker-compose](https://docs.docker.com/compose/install/) available on your computer.
Also, make sure to give enough resources for your Docker engine to prevent some errors from happening such as **exit status 137 (out of memory) docker**...

### Stats sample
You can check your docker Ressources stats by running `docker stats` command.
In this example, I have given 8.25G of ram to my docker engine and all container running together are eating at least 6G so it's highly recommended to give enough resources
<p align="center">
  <img src="https://raw.githubusercontent.com/LQss11/devops-pipeline/master/images/docker-stats.png" title="Jenkins pipeline">
</p>  

## Clone Repository
In order to run the app on your computer you will need to clone this repository (or just download the files), select a directory then run:
```sh
git clone https://github.com/LQss11/devops-pipeline.git
```
get inside the repository:
```sh
cd devops-pipeline
```

## Running the Stack
Now that you have files on your machine make sure you are on the same DIR as the docker-compose.yml file and run:
```sh
docker-compose up --build
```
This command will build Docker Images from a specified context and start all the containers with their required settings.

# Setup
## Tools
These are some of the Images we used to set up our stack, with the help of **Docker version 20.10.7**  using docker desktop on windows:

| Name | Version | Port Mapping |
| ------ | ------ | ------ |
| Apache Maven | 3.5.4 | |
| Sonatype Nexus3 | sonatype/nexus3:3.37.0 | 8001:8081 |
| Jenkins | jenkins/jenkins:lts | 8002:8080 |
| sonarqube | sonarqube:7.6-community | 8003:9000 |
| Mysqldb | mysql:5.7.32 | 3306:3306 |
| phpmyadmin | phpmyadmin/phpmyadmin:5.1.1 | 8004:80 |

## Jenkins Configuration
In this project the main goal was to make allow you setup and use jenkins easier so I created **default-user.groovy** script to setup initial admin user, also used **JAVA_OPTS -Djenkins.install.runSetupWizard=false** in the docker file to ignore initial secrets.

### Docker Hub Account
Setup your dockerhub credentials by updating the values of **DOCKER_USER** and **DOCKER_PASS** in `.env` file.
You can check the user by going to this url: http://localhost:8002/credentials/store/system/domain/_/.
### Mail setup
In order to use mailing service through your pipeline you can use the Jenkins **mailer plugin** then all you have to do is set up the mailing notification configuration like the following details:
  1. Go to Jenkins configuration page -> http://localhost:8002/configure
  1. Go all the way down to **E-mail Notification**
  1. **SMTP server**: smtp.gmail.com
  1. Click advanced
  1. Check **Use SMTP Authentication** and **Use SSL**
  1. **User Name**: email@gmail.com
  1. **Password**: email password
  1. **SMTP Port**: 465
  1. Finally check Test configuration by sending a test e-mail, type an email you want to test the service on then click `test configuration` -> you will receive a mail once you click it and that means the service works properly, and don't forget to save your settings.  

### Pipeline setup
Create A new Jenkins pipeline.
In `Build Triggers` check the `GitHub hook trigger for GITScm polling` box.
In `Pipeline -> Definition` select `Pipeline script from SCM ` then use these parameters:
  1. `Repository URL`:https://github.com/LQss11/devops-pipeline.git
  1. `Branch Specifier (blank for 'any')`:*/master
  1. `Script Path`:Jenkinsfile (you can choose another Jenkinsfile name and path if you are working with a different repository)  
  1. Once you finished setting up your project following this README.md file you will be able to run your project and see all stages progress. 

## Sonatype Nexus3 Configuration
Configuring Nexus is a bit similar to the first step of Jenkins where we will need to extract a secret to create our admin user.
  1. Visit http://localhost:8001.
  1. Click on **Sign in** on top right corner then enter **admin** username with secret from this command:
```sh
docker exec -it nexus3 /bin/bash -c "cat /nexus-data/admin.password"
```
  1. Chose a password for your administrator.

#### Notice
In this project username and password are used as admin admin in `/jenkins/settings.xml` and `Jenkinsfile` and `.env` for nexus deployment, if you wish to create different credentials make sure to change them as you have set them up in nexus.

In case you are building with Jenkins the same project again you will need to make sure that it does not exist in the maven releases.

## Ngrok Setup for GitHub webhooks
Working with GitHub webhooks wouldn't work if your Jenkins is not hosted on a server connected to the internet (localhost will not be allowed).
In order to solve that all you have to do is work with **Ngrok** --> [Download Link](https://ngrok.com/download).
To set it up simply type this cmd after running ngrok.
Let's say Jenkins is running on port **8002**.
```sh
ngrok HTTP 8002
```
now ngrok will generate HTTP and HTTPS links for the Jenkins server that will be available to use with GitHub webhooks.
our link would look something like this xxxx-xxx-xxx-xxx-xxx.ngrok.io

#### Github Setup
  1. Select the repository you want to work with.
  1. Go to `Settings`.
  1. In options select `Webhooks` the create a new webhook (link must look like this https://REPOSITORY_URL/settings/hooks/new).
  1. `Payload URL`: http://xxxx-xxx-xxx-xxx-xxx.ngrok.io/github-webhook/ 
  1. `Content type`: application/json
  1. `Which events would you like to trigger this webhook?`: Just the push event. 

Now once the project is fully set up once you push to that repository, GitHub will trigger that event to launch our Jenkins pipeline. 

# Env
Some of the variables are set up inside the .env file to make sure no one get access to that file since it contains most of the logins credentials

# Output
If your Jenkins pipeline is working properly this would be the output for each interface:

**Jenkins Pipeline Output**  
<p align="center">
  <img src="https://raw.githubusercontent.com/LQss11/devops-pipeline/master/images/Pipeline-Success-Failure.PNG" title="Jenkins pipeline">
</p> 

**Nexus Output**  
<p align="center">
  <img src="https://raw.githubusercontent.com/LQss11/devops-pipeline/master/images/Nexus.png" title="Nexus">
</p> 

**Sonarqube Output**  
<p align="center">
  <img src="https://raw.githubusercontent.com/LQss11/devops-pipeline/master/images/Sonarqube.png" title="Sonarqube">
</p> 

**Phpmyadmin Output**  
<p align="center">
  <img src="https://raw.githubusercontent.com/LQss11/devops-pipeline/master/images/Mysql.png" title="Mysql">
</p> 

## Jenkins Mailing Issues
Configuring the email service can be challenging sometimes once you encounter some annoying issues as happened to me
```sh
sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```
```sh
javax.mail.MessagingException: Could not connect to SMTP host: smtp.gmail.com, port: 465;
```
```sh
javax.mail.AuthenticationFailedException: 535-5.7.8 Username and Password not accepted
```
If you encountered one of these issues it is most likely that you haven't allowed your email sender to send an email through this service so all you have to do is login to your sender email then `Mail settings` -> `Security` -> `Enable less secure app access`, also make sure you have entered the right email and password if the error still persisting then it must be something related to your firewall stopping you from sending SMTP requests using Jenkins, so to solve this temporary all I had to do is disable my `Avast antivirus` for an hour and test the configuration and it worked like charm.

If you have any issues feel free to post your issue in the `Issues section` and I would be so happy to help you

# Information
Hope this project helped you solve a problem or create something that satisfies you in any way, feel free to contact me or post issues if you have any problems I would be more than happy to help.

## Help 
  1. In case using different spring boot project: change **artifact Id group Id and version** in Nexus stage inside the **Jenkinsfile**
  1. Spring Boot Datasource url connection: `spring.datasource.url=jdbc:mysql://db:3306/timesheet-devops-db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC`
  1. change `bat` to `sh` in the Jenkinsfile depending on your machine OS.