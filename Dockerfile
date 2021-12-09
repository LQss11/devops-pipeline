FROM openjdk:8-jdk-alpine
EXPOSE 8082
ADD http://nexus3:8081/repository/maven-releases/tn/esprit/spring/timesheet-devops/1.0/timesheet-devops-1.0.jar timesheet-devops-1.0.jar
ENTRYPOINT ["java","-jar","/timesheet-devops-1.0.jar"]
