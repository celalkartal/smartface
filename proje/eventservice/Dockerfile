FROM openjdk:17-alpine
#HEALTHCHECK icin kullanacagiz
RUN  apk add --no-cache curl \curl
LABEL maintainer="CELAL KARTAL"
ADD /target/eventservice-latest.jar eventservice.jar
ENTRYPOINT ["java","-jar","eventservice.jar"]