#FROM openjdk:11
#
#COPY target/spotify-assistant-0.0.1-SNAPSHOT.jar ./app.jar
#
#ENTRYPOINT ["java", "-jar", "./app.jar"]

FROM maven:3.6.0-jdk-11 AS build
COPY src /usr/app/src
COPY pom.xml /usr/app
RUN mvn -f /usr/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:11
COPY --from=build /usr/app/target/spotify-assistant-0.0.1-SNAPSHOT.jar /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/app.jar"]