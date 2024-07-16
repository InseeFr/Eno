FROM eclipse-temurin:21.0.3_9-jre
WORKDIR /opt/eno-ws/
COPY ./eno-ws/build/libs/*.jar /opt/eno-ws/eno-ws.jar
EXPOSE 8080

RUN addgroup eno
RUN useradd -g eno eno
USER eno

ENTRYPOINT ["java", "-jar",  "/opt/eno-ws/eno-ws.jar"]
