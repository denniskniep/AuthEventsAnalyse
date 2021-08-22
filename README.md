# Stream and analyse Auth Events

## Quickstart
```
sudo docker-compose -f docker-compose.flink.yml -f docker-compose.kafka.yml up
```

## Flink

### Cluster
https://ci.apache.org/projects/flink/flink-docs-stable/docs/deployment/resource-providers/standalone/docker/#flink-with-docker-compose

Flink runs as Application Cluster (see docker-compose.yaml)

Ui: http://localhost:8081 

### Application
Create Flink application skeleton
```
mvn archetype:generate \
    -DarchetypeGroupId=org.apache.flink \
    -DarchetypeArtifactId=flink-walkthrough-datastream-java \
    -DarchetypeVersion=1.13.0 \
    -DgroupId=de.denniskniep \
    -DartifactId=authEventsAnalyse \
    -Dversion=0.1 \
    -Dpackage=autheventsanalyse \
    -DinteractiveMode=false
```

## Kafka
https://docs.confluent.io/platform/current/kafka/introduction.html
https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html

Ui: http://localhost:9021


## Monitoring
With Grafana & Prometheus

Grafana Ui: http://localhost:3000 (admin:admin)
Prometheus Ui: http://localhost:9090

Kafka Monitoring: 
https://www.confluent.io/blog/monitor-kafka-clusters-with-prometheus-grafana-and-confluent/#prometheus


