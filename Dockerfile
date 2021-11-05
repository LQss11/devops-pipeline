FROM openjdk:8-jdk-alpine
EXPOSE 8082
ADD "C:\Program Files (x86)\Jenkins\workspace\salem\target\timesheet-devops-1.0.jar" timesheet-devops-1.0.jar/
ENTRYPOINT ["java","-jar","/timesheet-devops-1.0.jar"]