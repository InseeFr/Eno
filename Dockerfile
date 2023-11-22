FROM eclipse-temurin:17-jre-focal
WORKDIR /opt/eno-ws/
COPY ./eno-ws/build/libs/*.jar /opt/eno-ws/eno-ws.jar
EXPOSE 8080

RUN addgroup eno
RUN useradd -g eno eno
USER eno

ENTRYPOINT ["java", "-jar",  "/opt/eno-ws/eno-ws.jar"]
