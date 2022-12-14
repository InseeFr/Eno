FROM eclipse-temurin:17-jre-alpine
WORKDIR application
RUN rm -rf /application
COPY ./eno-ws/build/lib/eno-ws-3.0.0-SNAPSHOT.jar /application/
ENTRYPOINT ["java", "-jar",  "/application/eno-ws-3.0.0-SNAPSHOT.jar"]
