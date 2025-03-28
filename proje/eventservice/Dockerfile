FROM openjdk:17-alpine
LABEL maintainer="CELAL KARTAL"
ADD /target/eventservice-latest.jar eventservice.jar
ENTRYPOINT ["java","-jar","eventservice.jar"]