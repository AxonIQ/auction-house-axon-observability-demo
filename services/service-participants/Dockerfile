FROM azul/zulu-openjdk:17.0.7-jre

WORKDIR /app
ADD target/additionalFiles/opentelemetry-javaagent.jar opentelemetry-javaagent.jar

ADD target/service-participants-0.0.1-SNAPSHOT.jar service-participants.jar

ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent.jar", "-jar", "service-participants.jar"]
