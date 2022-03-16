FROM gradle:7.2.0-jdk11-hotspot as build
COPY --chown=gradle:gradle . /home/gradle
WORKDIR /home/gradle
RUN gradle build -x test

FROM openjdk:11-jdk

EXPOSE 8080/tcp

RUN mkdir /app

COPY --from=build /home/gradle/build/libs/IoT-DT-Backend-0.0.1.jar ./app.jar

ENTRYPOINT ["java","-jar","/app.jar"]