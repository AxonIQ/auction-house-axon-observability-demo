FROM azul/zulu-openjdk:17.0.7-jre

WORKDIR /app
RUN curl https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.23.0/opentelemetry-javaagent.jar -L -o opentelemetry-javaagent.jar

ADD target/service-auction-object-registry-0.0.1-SNAPSHOT.jar service-auction-object-registry.jar

ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent.jar", "-jar", "service-auction-object-registry.jar"]
