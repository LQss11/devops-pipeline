# Devops-pipeline
`Devios-pipeline` help you set Up and automate a Continuous Integration & Delivery (CI/CD) process with Docker and Docker-compose.
# Quick Start
## Prerequisites 
This project uses a set application but don't worry all of them are dockerized you will only need to have [Docker](https://docs.docker.com/engine/install/) and [Docker-compose](https://docs.docker.com/compose/install/) available on your computer.
Also make sure to give enough ressources for your Docker engine to prevent some errors from happening such as **exit status 137 (out of memory) docker**...
### Stats sample
You can check your docker ressources stats by running `docker stats` command.
In this example I have given 8.25G of ram to my docker engine and all container running together are eating at least 6G so it's highly recommended to give enough ressources
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
This command will build docker Images from specified context and start all the containers with their required settings.
# Setup
## Tools
These are some of the Images we used to setup our stack, with the help of **Docker version 20.10.7**  using docker desktop on windows:
| Name | Version | Port Mapping |
| ------ | ------ | ------ |
| Apache Maven | 3.5.4 | |
| Sonatype Nexus3 | sonatype/nexus3:3.37.0 | 8001:8081 |
| Jenkins | jenkins/jenkins:lts | 8002:8080 |
| sonarqube | sonarqube:7.6-community | 8003:9000 |
| Mysqldb | mysql:5.7.32 | 3306:3306 |
| phpmyadmin | phpmyadmin/phpmyadmin:5.1.1 | 8004:80 |
## Jenkins Configuration
Once your stack up and ready you will need to setup your jenkins environment, and the first thing you will be asked to do is to unlock Jenkins using a secret password you by visiting http://localhost:8002.
 To get the secret you will need to execute this command:
```sh
docker exec -it jenkins /bin/bash -c "cat /var/jenkins_home/secrets/initialAdminPassword"
```
The secret will look something like `8d8089564a0d4ec99182814fecafb5b8` copy paste it into the password field.

Now that you have unlocked jenkins, in the next page select **Install suggested plugins**, wait for it to finish then create you first admin user.
### Docker Pipeline plugin installation
In this project we are going to use the **Docker Pipeline plugin** in order to run docker commands in the pipeline with the help of docker engine's Unix Socket or **docker.sock**
  1. http://localhost:8002/pluginManager/available
  1. Available.
  1. Search Docker pipeline and select the plugin.
  1. Install without restart.
  1. Once plugin installed click on **Restart Jenkins when installation is complete and no jobs are running** checkbox.
### Docker Hub Account
In order to push images into your docker hub repository you will need to set up credentials for jenkins by following these steps:
  1. visit http://localhost:8002/credentials/store/system/domain/_/newCredentials
  1. Set dockerhub **username**
  1. Set dockerhub **password**
  1. Set dockerhub Id which in fact the key that will be used in the Jenkinsfile in our case it's **dockerHub**
  1. Set dockerhub description for example: **Docker Hub Account** then click ok
### Mail setup
In order to use mailing service through your pipeline you can use the jenkins **mailer plugin** then all you have to do is set up the mailing notification configuration as the following details:
  1. Go to Jenkins configuration page -> http://localhost:8002/configure
  1. Go all the way down to **E-mail Notification**
  1. **SMTP server**: smtp.gmail.com
  1. Click advanced
  1. Check **Use SMTP Authentication** and **Use SSL**
  1. **User Name**: email@gmail.com
  1. **Password**: email password
  1. **SMTP Port**: 465
  1. Finally ckeck Test configuration by sending test e-mail, type an email you want to test the service on then click `test configuration` -> you will recieve a mail once you click it and that means the service works properly, and don't forget to save your settings.  
### Pipeline setup
Create A new jenkins pipeline.
In `Build Triggers` check the `GitHub hook trigger for GITScm polling` box.
In `Pipeline -> Definition` select `Pipeline script from SCM ` then use these parameters:
  1. `Repository URL`:https://github.com/LQss11/devops-pipeline.git
  1. `Branch Specifier (blank for 'any')`:*/master
  1. `Script Path`:Jenkinsfile (you can chose another Jenkinsfile name and path if you are working with a different repository)  
  1. Once you finished setting up your project following this README.md file you will be able to run your project and see all stages progress. 
## Sonatype Nexus3 Configuration
Configuring Nexus is a bit similar to the first step of Jenkins where we will need to extract a secret to create our admin user.
  1. Visit http://localhost:8002.
  1. Click on **Sign in** on top right corner then enter **admin** username with secret from this command:
```sh
docker exec -it nexus3 /bin/bash -c "cat /nexus-data/admin.password"
```
  1. Chose a password for your adminstrator.
#### Notice
In this project username and password are used as admin admin in `/jenkins/settings.xml` and `Jenkinsfile` and `.env` for nexus deployement, if you wish to create different credentials make sure to change them as you have set them up in nexus.

In case you are building with jenkins the same project again you will need to make sure that it does not exist in the maven releases.

# Env
Some of the variables are setup inside the .env file make sure noone get access to that file since it contains most of the logins credentials

# Output
If your Jenkins pipeline is working properly this would be the output for each interface:
**Jenkins Pipeline Output**  
<p align="center">
  <img src="https://raw.githubusercontent.com/LQss11/devops-pipeline/master/images/Pipeline-Success-Failure.png" title="Jenkins pipeline">
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

# Information
Hope this project helped you solve a problem or create something that satisfy you in any way, feel free to contact me or post issues if you have any problem I would be more than happy to help.
In case you are willing to use a different spring boot project you will need to update some of the variables such as **artifact Id group Id and version** inside the jenkinsfile and more specifically in the nexus stage, also specify the branch you are cloning in jenkinsfile.
Also as well as the database connection in the **application.properties** file which in this project we used this datasource 
spring.datasource.url=jdbc:mysql://db:3306/timesheet-devops-db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC