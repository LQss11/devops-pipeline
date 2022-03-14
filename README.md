# Devops-pipeline
`DevOps-pipeline` helps you set Up and automate a Continuous Integration & Delivery (CI/CD) process with Docker and Docker-compose.

## Prerequisites 
This project uses a set of open source containerzed applications, you will only need to have [Docker](https://docs.docker.com/engine/install/) and [Docker-compose](https://docs.docker.com/compose/install/) available on your computer.
Also.

>make sure to give enough resources for your Docker engine to prevent some errors from happening such as **exit status 137 (out of memory) docker**...

### Stats sample
You can check your docker Ressources stats by running `docker stats` command.
In this example, I have given 8.25G of ram to my docker engine and all container are running together are using at least 6G so it's highly recommended to give enough resources.
<p align="center">
  <img src="https://raw.githubusercontent.com/LQss11/devops-pipeline/master/images/docker-stats.png" title="Jenkins pipeline">
</p>  

# Quick Start
## Clone Repository
To run the app on your computer you will need to clone this repository by running:
```sh
git clone https://github.com/LQss11/devops-pipeline.git
```
then :
```sh
cd devops-pipeline
```

## Running the Stack
In order to run the stack we will be running our **docker compose configuration**:
```sh
docker-compose up --build
```
>Don't forget to check and update `.env` file if necessary. 

# Setup
## Tools
These are some of the Images we used to set up our stack:

| Name            | Version                     | Port Mapping |
| --------------- | --------------------------- | ------------ |
| Docker          | 20.10.7                     |              |
| Apache Maven    | 3.5.4                       |              |
| Sonatype Nexus3 | sonatype/nexus3:3.37.0      | 8001:8081    |
| Jenkins         | jenkins/jenkins:lts         | 8002:8080    |
| sonarqube       | sonarqube:7.6-community     | 8003:9000    |
| phpmyadmin      | phpmyadmin/phpmyadmin:5.1.1 | 8004:80      |
| Mysqldb         | mysql:5.7.32                | 3306:3306    |


# Jenkins Configuration
### Init Script
- **default-user.groovy** : Setup initial admin user.
- **dockerhub-cred.groovy** : Setup Dockerhub credentials (Make sure to update yours on `.env`).

## Mail setup
To set up the mailing notification configuration you can either set it up **manually** by going to jenkins configuration or using **JCasC** :
### Manual Mailing Configuration
  1. Go to Jenkins configuration page -> http://localhost:8002/configure
  1. Go all the way down to **E-mail Notification**
  1. **SMTP server**: smtp.gmail.com
  1. Click advanced
  1. Check **Use SMTP Authentication** and **Use SSL**
  1. **User Name**: email@gmail.com
  1. **Password**: email password
  1. **SMTP Port**: 465
  1. Finally check Test configuration by sending a test e-mail, type an email you want to test the service on then click `test configuration` -> you will receive a mail once you click it and that means the service works properly, and don't forget to save your settings. 
### Jenkins configuration as code Mailing Configuration
JCasC make it possible to configure jenkins as code through a yaml file like `/jenkins/mailer-config.yaml`.

Now all you have to worry about is the following:
- **username** : plain text
- **password** : encrypted password (AES-128) you will to run the `ENCRYPTION SCRIPT` script in the script field by going to `http://localhost:8002/script`.

**ENCRYPTION SCRIPT**
```groovy
import hudson.util.Secret

def secret = Secret.fromString("Your Password")
println(secret.getEncryptedValue())
```
Once done simply copy/paste result into the Config file password field.

Now we are going to simply use that file to apply the new mail configuration by visiting:
```
http://localhost:8002/configuration-as-code/
```
then copy the configuration file path `/var/jenkins_home/JCasC/mailer-conf.yaml` (or URL) and finally, apply a new 

>The used path is the bind mount specified in the docker compose jenkins volumes configuration.


> You can use the JCasC for your desire find more [here](https://plugins.jenkins.io/configuration-as-code/)

## Pipeline setup
### Automatic setup
The pipeline settings are already setup automatically by copying them to `/var/jenkins_home/jobs` you can save yours by creating pipeline then saving its data on your host machine then bind mount it again in that directory.
### Manual
For this project I used the **spring-for-jenkins-with-docker** branch which has a spring boot project to be built with maven you can either copy `Jenkinsfile` in the pipeline or just follow these steps.

As well as for the pipeline setup 
Create A new Jenkins pipeline.
In `Build Triggers` check the `GitHub hook trigger for GITScm polling` box.
In `Pipeline -> Definition` select `Pipeline script from SCM ` then use these parameters:
  1. `Repository URL`:https://github.com/LQss11/devops-pipeline.git
  2. `Branch Specifier (blank for 'any')`:*/master
  3. `Script Path`:Jenkinsfile (you can choose another Jenkinsfile name and path if you are working with a different repository)  
  4. Once you finished setting up your project following this README.md file you will be able to run your project and see all stages progress. 

# Sonatype Nexus3 Configuration
Configuring Nexus is a bit similar to the first step of Jenkins where we will need to extract a secret to create our admin user.
  1. Visit http://localhost:8001.
  1. Click on **Sign in** on top right corner then enter **admin** username with secret from this command:
```sh
docker exec -it nexus3 /bin/bash -c "cat /nexus-data/admin.password"
```
  1. Chose a password for your administrator.

### Notice
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

### Github Setup for ngrok
  1. Select the repository you want to work with.
  1. Go to `Settings`.
  1. In options select `Webhooks` the create a new webhook (link must look like this https://REPOSITORY_URL/settings/hooks/new).
  1. `Payload URL`: http://xxxx-xxx-xxx-xxx-xxx.ngrok.io/github-webhook/ 
  1. `Content type`: application/json
  1. `Which events would you like to trigger this webhook?`: Just the push event. 

Now once the project is fully set up once you push to that repository, GitHub will trigger that event to launch our Jenkins pipeline. 

# Env
Some of the variables are set up inside the .env file to make sure no one gets access to that file since it contains most of the logins credentials

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