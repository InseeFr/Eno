FROM eclipse-temurin:17-jre-alpine
WORKDIR application
RUN rm -rf /application
COPY ./tmp/eno-ws.jar /opt/eno-ws/
ENTRYPOINT ["java", "-jar",  "/opt/eno-ws/eno-ws.jar"]
