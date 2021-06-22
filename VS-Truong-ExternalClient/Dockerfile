##
## Build stage
##

FROM maven:3.8.1-openjdk-11-slim AS build
LABEL maintainer=lam-tung.truong@stud.h-da.de
LABEL stage=build

RUN rm -f /app/src
RUN mkdir -p /app/
COPY VS-Truong-ExternalClient/src/ /app/src/
COPY VS-Truong-ExternalClient/pom.xml /app/
COPY pom.xml /pom.xml
WORKDIR /app
RUN mvn clean package

##
## Run stage
##

FROM openjdk:11.0-jre-slim
LABEL maintainer=lam-tung.truong@stud.h-da.de
LABEL stage=run

ARG NAME
ENV ENV_NAME=${NAME}
ARG TPORT
ENV ENV_TPORT=${TPORT}

RUN rm -f /app
RUN mkdir -p /app
COPY --from=build /app/target/VS-Truong-ExternalClient-1.0-SNAPSHOT-jar-with-dependencies.jar /app
CMD java -jar /app/VS-Truong-ExternalClient-1.0-SNAPSHOT-jar-with-dependencies.jar $ENV_NAME $ENV_TPORT

