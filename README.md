# Auction House Axon Observability Demo

Full-fledged microservices demo using Axon Framework and Axon Server features, that's fully monitored using OpenTelemetry,
Prometheus and Grafana, and [Inspector Axon](https://inspector.axoniq.io). The demo leverages:

- Axon Server clustering and messaging
- [Inspector Axon](https://inspector.axoniq.io) monitoring
- Prometheus scraping and Grafana dashboarding
- OpenTelemetry tracing with OpenSearch and Jaeger query
- Axon FireStarter to introduce chaos into Axon Framework applications
- A landing page with observability exercises and reverse-proxy links to all features and deployments

## How to run

You will need to put an `axoniq.license` file in the `infrastructure` repository that contains a valid license for Axon Server EE.
You can request a [free trial license here](https://www.axoniq.io/axon-server-trial).

Now, you can run this demo using `docker compose`. Please execute the following:
```shell
docker compose -f infrastructure/full/docker-compose.yaml up -d
# or
docker compose -f infrastructure/light/docker-compose.yaml up -d
```

This will run a full stack locally, with:
- OpenSearch
- Jaeger
- Prometheus
- Grafana
- Axon Server Enterprise (3x)
- Nginx http://localhost:8080
- Service: Participants
- Service: Auctions
- Service: Auction Query
- Service: Interfaces
- Service: Object Registry

When everything has been booted, navigate to the [landing page](http://localhost:8080).
