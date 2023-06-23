FROM openjdk:17.0.2

WORKDIR /app
RUN curl https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.23.0/opentelemetry-javaagent.jar -L -o opentelemetry-javaagent.jar

ADD target/service-auctions-0.0.1-SNAPSHOT.jar service-auctions.jar

ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent.jar", "-jar", "service-auctions.jar"]
