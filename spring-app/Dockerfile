# Base Image ubuntu
FROM ubuntu:18.04

# Installing dependencies
RUN apt-get update
RUN apt-get install -y openjdk-8-jdk maven 

# coping the required application files
COPY . /spring-app
WORKDIR /spring-app/app-content

# building the spring boot app
RUN mvn clean install

# Changing the working directory
WORKDIR /spring-app/app-content/target

EXPOSE 8082

# running the spring boot app
CMD ["java","-jar","timesheet-devops-1.0.jar"]