FROM openjdk:22
LABEL authors="francescomarastoni"
ADD ./target/PassportEase-0.0.1-SNAPSHOT.jar /usr/src/PassportEase-0.0.1-SNAPSHOT.jar
WORKDIR usr/src
ENTRYPOINT ["java","-jar", "PassportEase-0.0.1-SNAPSHOT.jar"]