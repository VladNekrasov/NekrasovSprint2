FROM gradle:8.2-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/NekrasovSprint2-all.jar
ENTRYPOINT ["java","-jar","/app/NekrasovSprint2-all.jar"]