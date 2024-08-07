FROM eclipse-temurin:21.0.4_7-jre

ENV PATH_TO_JAR=/opt/eno-ws/eno-ws.jar
WORKDIR /opt/eno-ws/
ADD ./eno-ws/build/libs/*.jar $PATH_TO_JAR

ENV JAVA_TOOL_OPTIONS_DEFAULT \
    -XX:MaxRAMPercentage=75 \
    -XX:+UseZGC

ENTRYPOINT [ "/bin/sh", "-c", \
    "export JAVA_TOOL_OPTIONS=\"$JAVA_TOOL_OPTIONS_DEFAULT $JAVA_TOOL_OPTIONS\"; \
    exec java -jar $PATH_TO_JAR" ]
