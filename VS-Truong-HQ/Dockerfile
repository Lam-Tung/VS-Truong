##
## Build stage
##

FROM maven:3.8.1-openjdk-11-slim AS build
LABEL maintainer=lam-tung.truong@stud.h-da.de
LABEL stage=build

RUN rm -f /app
RUN mkdir -p /app/
COPY VS-Truong-HQ/src/ /app/src
COPY VS-Truong-HQ/pom.xml /app/
WORKDIR /app
RUN mvn clean package

##
## Run stage
##

FROM openjdk:11.0-jre-slim
LABEL maintainer=lam-tung.truong@stud.h-da.de
LABEL stage=run

EXPOSE 6543/udp
EXPOSE 8080/tcp
ARG NAME
ENV ENV_NAME=${NAME}

RUN mkdir -p /app
COPY --from=build /app/target/VS-Truong-HQ-1.0-SNAPSHOT-jar-with-dependencies.jar /app
CMD ["java", "-jar", "/app/VS-Truong-HQ-1.0-SNAPSHOT-jar-with-dependencies.jar", "$ENV_NAME"]
