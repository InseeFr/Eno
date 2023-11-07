FROM eclipse-temurin:17-jre-focal
WORKDIR /opt/eno-ws/
COPY ./eno-ws/build/libs/*.jar /opt/eno-ws/eno-ws.jar
ENTRYPOINT ["java", "-jar",  "/opt/eno-ws/eno-ws.jar"]
